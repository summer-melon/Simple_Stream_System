package com.toutiao.melon.master.job;

import com.google.common.collect.Lists;
import com.toutiao.melon.api.IJob;
import com.toutiao.melon.api.IOutStream;
import com.toutiao.melon.api.ISource;
import com.toutiao.melon.api.job.Edge;
import com.toutiao.melon.api.job.Job;
import com.toutiao.melon.api.job.JobException;
import com.toutiao.melon.api.job.Node;
import com.toutiao.melon.api.stream.OutGoingStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JobLoader {

    private Map<String, Node> nodes;
    private Map<String, List<Edge>> graph;

    public ComputationGraph load(String topologyName, URL jarLocalUrl) throws Throwable {
        URL[] url = {jarLocalUrl};
        try (URLClassLoader loader = URLClassLoader.newInstance(url)) {
            loadTopologyDefinition(topologyName, loader, jarLocalUrl);
            validateTopology(topologyName, loader);
            String sourceId = getSourceId(loader);
            // nodeId => TaskDefinition
            Map<String, TaskDefinition> tasks = detectCycleAndConnectivity(sourceId);
            ComputationGraph computationGraph =
                    new ComputationGraph(tasks, getAssignOrder(sourceId));

            // add acker
            computationGraph.getAssignOrder().add("~acker");
            TaskDefinition ackerTask = new TaskDefinition();
            ackerTask.setProcessNum(1);
            ackerTask.setThreadsPerProcess(3);
            ackerTask.setInboundStreamIds(Lists.newArrayList(topologyName + "-~ackerInbound"));
            computationGraph.getTasks().put("~acker", ackerTask);
            return computationGraph;
        }
    }

    private static class DfsState<T> {
        private final T key;
        private final int edgeIndex;

        public DfsState(T key, int edgeIndex) {
            this.key = key;
            this.edgeIndex = edgeIndex;
        }

        public DfsState<T> nextEdge() {
            return new DfsState<>(key, edgeIndex + 1);
        }
    }

    private String getSourceId(URLClassLoader loader)
            throws ClassNotFoundException, JobException {
        for (Map.Entry<String, Node> nodeDef : nodes.entrySet()) {
            Class<?> nodeClass = loader.loadClass(nodeDef.getValue().getClassName());
            if (ISource.class.isAssignableFrom(nodeClass)) {
                return nodeDef.getKey();
            }
        }
        throw new JobException("No source");
    }

    // Assign order of process, tasks with multiple process will appeared multiple
    // times in the returned list. Null will be inserted between two task names if
    // they are not adjacent in the DAG.
    private List<String> getAssignOrder(String sourceId) {
        List<String> retOrder = new ArrayList<>();
        Deque<DfsState<TaskInstance>> dfsStack = new ArrayDeque<>();
        Map<TaskInstance, List<TaskInstance>> augmentedGraph = augmentGraph();
        for (int replicaId = 0; ; ++replicaId) {
            TaskInstance sourceInstance = new TaskInstance(sourceId, replicaId);
            if (augmentedGraph.containsKey(sourceInstance)) {
                dfsStack.push(new DfsState<>(sourceInstance, 0));
                retOrder.add(sourceId);
            } else {
                break;
            }
        }

        Set<TaskInstance> visited = new HashSet<>();
        while (!dfsStack.isEmpty()) {
            DfsState<TaskInstance> state = dfsStack.pop();
            List<TaskInstance> edges = augmentedGraph.get(state.key);
            if (state.edgeIndex < edges.size()) {
                dfsStack.push(state.nextEdge());
            } else {
                continue;
            }
            TaskInstance thisInstance = edges.get(state.edgeIndex);
            if (visited.contains(thisInstance)) {
                retOrder.add(null);
                continue;
            }
            visited.add(thisInstance);
            retOrder.add(thisInstance.getNodeId());
            List<TaskInstance> thisInstanceEdges = augmentedGraph.get(thisInstance);
            if (thisInstanceEdges == null) {
                retOrder.add(null);
            } else {
                dfsStack.push(new DfsState<>(thisInstance, 0));
            }
        }

        return retOrder;
    }

    // Convert a graph like A -> B(with 2 processes) -> C to
    // A#0 -> B#0 -> C#0 plus A#0 -> B#1 -> C#0.
    // SourceId#0 is the only instance ensured for source.
    private Map<TaskInstance, List<TaskInstance>> augmentGraph() {
        // instances associated to nodeId
        Map<String, Set<TaskInstance>> instanceMap = new HashMap<>();
        Map<TaskInstance, Set<TaskInstance>> retGraph = new HashMap<>();
        List<String> sortedNodes = getReverseTopologicalOrder();
        for (String n : sortedNodes) {
            int processNum = nodes.get(n).getProcessNum();
            Set<TaskInstance> nextInstances = new HashSet<>();
            if (graph.containsKey(n)) { // not end node of DAG
                graph.get(n).stream() //edges
                        .map(Edge::getTargetId)
                        .forEach(x -> {
                            if (instanceMap.containsKey(x)) {
                                nextInstances.addAll(instanceMap.get(x));
                            }
                        });
                IntStream.range(0, processNum)
                        .forEach(x -> retGraph.put(new TaskInstance(n, x), nextInstances));
                instanceMap.put(n, nextInstances);
            }

            instanceMap.put(n, IntStream.range(0, processNum)
                    .mapToObj(x -> new TaskInstance(n, x))
                    .collect(Collectors.toSet()));
        }

        return retGraph.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, x -> new ArrayList<>(x.getValue())));
    }

    private List<String> getReverseTopologicalOrder() {
        Map<String, Integer> outboundEdgesCount = new HashMap<>();
        Map<String, List<String>> prevNodeMap = new HashMap<>();
        Queue<String> processQueue = new ArrayDeque<>();
        for (String nodeName : nodes.keySet()) {
            if (!graph.containsKey(nodeName)) {
                processQueue.add(nodeName);
            }
        }
        for (Map.Entry<String, List<Edge>> e : graph.entrySet()) {
            outboundEdgesCount.put(e.getKey(), e.getValue().size());

            for (Edge edge : e.getValue()) {
                if (!prevNodeMap.containsKey(edge.getTargetId())) {
                    prevNodeMap.put(edge.getTargetId(), new ArrayList<>());
                }
                prevNodeMap.get(edge.getTargetId()).add(e.getKey());
            }
        }

        // reversed topological sorted
        List<String> sorted = new ArrayList<>();
        while (!processQueue.isEmpty()) {
            String e = processQueue.poll();
            sorted.add(e);
            if (prevNodeMap.containsKey(e)) {
                List<String> prevNodes = prevNodeMap.get(e);
                for (String prevNode : prevNodes) {
                    int newValue = outboundEdgesCount.get(prevNode) - 1;
                    if (newValue == 0) {
                        processQueue.add(prevNode);
                        outboundEdgesCount.remove(prevNode);
                    } else {
                        outboundEdgesCount.put(prevNode, newValue);
                    }
                }
            }
        }

        return sorted;
    }

    private void loadTopologyDefinition(
            String topologyName, URLClassLoader loader, URL jarLocalUrl) throws Throwable {
        Class<?> mainClass;
        try (JarFile jarFile = new JarFile(jarLocalUrl.getFile())) {
            String mainClassName = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
            mainClass = loader.loadClass(mainClassName);
            Job topology = ((IJob) mainClass.getDeclaredConstructor().newInstance()).getJob();
            nodes = topology.getNodes();
            graph = topology.getEdges();

            // add "topologyName-" prefix for streamId
            graph.values().forEach(li ->
                    li.forEach(edge -> edge.setStreamId(topologyName + "-" + edge.getStreamId())));
        }
    }

    private void validateTopology(String topologyName, URLClassLoader loader) throws Throwable {
        for (Map.Entry<String, List<Edge>> e : graph.entrySet()) {
            String sourceId = e.getKey();

            Class<?> sourceClass = loader.loadClass(nodes.get(sourceId).getClassName());
            OutGoingStream declarer = new OutGoingStream(topologyName, sourceId);
            ((IOutStream) sourceClass.getDeclaredConstructor()
                    .newInstance()).defineOutGoingStream(declarer);
            Set<String> schemaNames = declarer.getOutGoingStreamSchemas().keySet();
            if (schemaNames.size() != e.getValue().size()) {
                throw new Exception("Number of schemas defined in '" + sourceId
                        + "' doesn't match its output stream numbers");
            }

            Set<String> targetIds = new HashSet<>();
            Set<String> streamIds = new HashSet<>();
            for (Edge d : e.getValue()) {
                String targetId = d.getTargetId();
                if (targetIds.contains(targetId)) {
                    throw new JobException("TargetId '" + targetId
                            + "' mentioned more than once in streams from '" + sourceId + "'");
                }
                targetIds.add(targetId);

                String streamId = d.getStreamId();
                String realStreamId = streamId.split("-")[1];
                if (streamIds.contains(streamId)) {
                    throw new JobException("StreamId '" + realStreamId
                            + "' mentioned more than once in streams from '" + sourceId + "'");
                }
                streamIds.add(streamId);

                if (!nodes.containsKey(sourceId)) {
                    throw new JobException("Unknown sourceId in stream declaration: " + sourceId);
                }
                if (!nodes.containsKey(targetId)) {
                    throw new JobException("Unknown targetId in stream declaration: " + targetId);
                }

                if (!schemaNames.contains(streamId)) {
                    throw new JobException("Stream with id '" + realStreamId
                            + "' doesn't exist in source node with id '" + sourceId + "'");
                }
            }
        }
    }

    // Refer to CLRS, returns a Map, which maps nodeId => TaskDefinition
    private Map<String, TaskDefinition> detectCycleAndConnectivity(String sourceId)
            throws JobException {
        Map<String, TaskDefinition> grayNodes = new HashMap<>();
        Map<String, TaskDefinition> blackNodes = new HashMap<>();
        grayNodes.put(sourceId, new TaskDefinition(nodes.get(sourceId), graph.get(sourceId)));
        Deque<DfsState<String>> states = new ArrayDeque<>();
        states.push(new DfsState<>(sourceId, 0));

        while (!states.isEmpty()) {
            DfsState<String> s = states.pop();
            List<Edge> edges = graph.get(s.key);
            if (s.edgeIndex >= edges.size()) {
                TaskDefinition t = grayNodes.get(s.key);
                grayNodes.remove(s.key);
                blackNodes.put(s.key, t);
                continue;
            } else {
                states.push(s.nextEdge());
            }
            String thisNode = edges.get(s.edgeIndex).getTargetId();
            if (grayNodes.containsKey(thisNode)) {
                throw new JobException("Cycle detected in topology");
            }
            if (blackNodes.containsKey(thisNode)) {
                continue;
            }

            if (graph.containsKey(thisNode)) {
                grayNodes.put(thisNode,
                        new TaskDefinition(nodes.get(thisNode), graph.get(thisNode)));
                states.push(new DfsState<>(thisNode, 0));
            } else {
                blackNodes.put(thisNode,
                        new TaskDefinition(nodes.get(thisNode), new ArrayList<>()));
            }
        }

        if (blackNodes.size() != nodes.size()) {
            throw new JobException(
                    "Some operators aren't connected with source directly or indirectly");
        }

        for (List<Edge> l : graph.values()) {
            for (Edge e : l) {
                TaskDefinition t = blackNodes.get(e.getTargetId());
                t.addInboundStream(e.getStreamId());
                if (t.getInboundStreamIds().size() != 1) {
                    throw new JobException("Only one inbound stream is allowed for each operator");
                }
            }
        }
        return blackNodes;
    }
}
