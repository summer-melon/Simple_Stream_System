package com.toutiao.melon.master.service;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

import com.toutiao.melon.master.job.ComputationGraph;
import com.toutiao.melon.master.job.TaskDefinition;
import com.toutiao.melon.shared.util.SharedUtil;
import com.toutiao.melon.shared.wrapper.ZooKeeperConnection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ZooKeeperService {

    private final ZooKeeperConnection zkConn;
    private static final Logger log = LoggerFactory.getLogger(ZooKeeperService.class);

    @Inject
    public ZooKeeperService(ZooKeeperConnection zkConn) {
        this.zkConn = zkConn;
        init();
    }

    public void init() {
        String masterAddr = null;
        try {
            masterAddr = SharedUtil.getHost() + ":6000";
        } catch (IOException e) {
            log.error("Cannot get host IP: " + e.toString());
            System.exit(-1);
        }

        zkConn.create("/master", masterAddr);
        if (!zkConn.create("/master/lock", null, CreateMode.EPHEMERAL)) {
            log.error("A master is already running");
            System.exit(-1);
        }
        if (!masterAddr.equals(zkConn.get("/master"))) {
            zkConn.set("/master", masterAddr);
        }

        zkConn.create("/master/job", null);
        zkConn.create("/worker", null);
        // for task assignment, persistent children
        zkConn.create("/worker/registered", null);
        // for worker registration, ephemeral children
        zkConn.create("/worker/available", null);
        // for storing workers' data
        zkConn.create("/worker/nodeData", null);
        zkConn.create("/stream", null);
        zkConn.create("/exporter", null);
    }

    public boolean jobExists(String jobName) {
        return zkConn.exists("/master/job/" + jobName);
    }

    private static class LoadInfo {
        private String workerId;
        private int threads;
        private List<String> newAssignments = new ArrayList<>();

        public String getWorkerId() {
            return workerId;
        }

        public void setWorkerId(String workerId) {
            this.workerId = workerId;
        }

        public int getThreads() {
            return threads;
        }

        public void setThreads(int threads) {
            this.threads = threads;
        }

        public List<String> getNewAssignments() {
            return newAssignments;
        }

        public void setNewAssignments(List<String> newAssignments) {
            this.newAssignments = newAssignments;
        }

        public LoadInfo(String workerId, int threads) {
            this.workerId = workerId;
            this.threads = threads;
        }
    }

    public synchronized void startJob(String jobName, ComputationGraph computationGraph)
            throws InterruptedException, KeeperException {
        List<String> availableWorkers = zkConn.getChildren("/worker/available");
        if (availableWorkers.isEmpty()) {
            throw new RuntimeException("No workers available");
        }
        AtomicInteger totalAssignedThreads = new AtomicInteger(computationGraph.getTotalThreads());
        Queue<LoadInfo> loadInfos =
                new PriorityQueue<>(Comparator.comparingInt(LoadInfo::getThreads));
        availableWorkers.stream()
                .map(x -> {
                    String load = zkConn.get("/worker/registered/" + x);
                    if (load != null) {
                        int assignedThreads = Integer.parseInt(load);
                        totalAssignedThreads.addAndGet(assignedThreads);
                        return new LoadInfo(x, assignedThreads);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .forEach(loadInfos::add);
        double avgThreads = (double) totalAssignedThreads.get() / loadInfos.size();
        List<String> assignOrder = computationGraph.getAssignOrder();
        Map<String, TaskDefinition> tasks = computationGraph.getTasks();

        Map<String, Integer> encodeHelper = new HashMap<>();
        Function<String, String> encodeAssignment = taskName -> {
            TaskDefinition taskDef = tasks.get(taskName);
            int threadNum = taskDef.getThreadsPerProcess();
            String inboundStr = taskDef.getInboundStreamIds().stream()
                    .reduce("", (l, r) -> r + ";" + l);
            String outboundStr = taskDef.getOutboundStreamIds().stream()
                    .reduce("", (l, r) -> r + ";" + l);
            if (!encodeHelper.containsKey(taskName)) {
                encodeHelper.put(taskName, -1);
            }
            int processIndex = encodeHelper.get(taskName) + 1;
            encodeHelper.put(taskName, processIndex);
            return jobName + "#" + taskName + "#" + processIndex + "#" + threadNum
                    + "#" + inboundStr + "#" + outboundStr;
        };

        int tmpThreads = 0;
        int assignedNodeCount = 0;
        List<String> tmpInstances = new ArrayList<>();
        for (int i = 0; i < assignOrder.size(); i++) {
            String taskName = assignOrder.get(i);
            if (taskName == null) {
                continue; // ignore null separators
            }
            LoadInfo load = loadInfos.peek();
            if (load == null) {
                throw new RuntimeException("Internal Error: loadInfo.peek() == null");
            }
            int curThreads = tasks.get(taskName).getThreadsPerProcess();
            if (assignedNodeCount == loadInfos.size() - 1
                    || tmpThreads + curThreads + load.getThreads() < avgThreads) {
                tmpThreads += curThreads;
                tmpInstances.add(encodeAssignment.apply(taskName));
            } else {
                double prevDelta = avgThreads - tmpThreads;
                double curDelta = tmpThreads + curThreads - avgThreads;
                if (prevDelta <= curDelta && tmpThreads != 0) {
                    --i;
                } else {
                    tmpThreads += curThreads;
                    tmpInstances.add(encodeAssignment.apply(taskName));
                }
                loadInfos.poll();
                load.threads += tmpThreads;
                load.newAssignments.addAll(tmpInstances);
                loadInfos.add(load);
                ++assignedNodeCount;
                tmpThreads = 0;
                tmpInstances.clear();
            }
        }
        if (!tmpInstances.isEmpty()) {
            LoadInfo load = loadInfos.peek();
            loadInfos.poll();
            if (load == null) {
                throw new RuntimeException("Internal Error: loadInfo.peek() == null");
            }
            load.threads += tmpThreads;
            load.newAssignments.addAll(tmpInstances);
            loadInfos.add(load);
        }

        Transaction txn = zkConn.transaction();
        while (!loadInfos.isEmpty()) {
            LoadInfo load = loadInfos.poll();
            String workerPath = "/worker/registered/" + load.getWorkerId();
            txn.setData(workerPath, Integer.toString(load.getThreads()).getBytes(), -1);
            for (String assignment : load.getNewAssignments()) {
                txn.create(workerPath + "/" + assignment, null,
                        OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }
        txn.create("/master/job/" + jobName, "run".getBytes(),
                OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        try {
            txn.commit();
        } catch (Exception e) {
            log.warn("The exception occurs, but skip now.");
        }

    }

    public void stopJob(String jobName) {
        String jobPath = "/master/job/" + jobName;
        zkConn.set(jobPath, "stop");
        String registeredPath = "/worker/registered";
        List<String> registeredWorkers = zkConn.getChildren(registeredPath);

        // delete assigned tasks
        for (String worker : registeredWorkers) {
            String workerPath = registeredPath + "/" + worker;
            int threadNum = Integer.parseInt(zkConn.get(workerPath));
            List<String> assignedTasks = zkConn.getChildren(workerPath);
            for (String task : assignedTasks) {
                if (task.startsWith(jobName + "#")) {
                    // [0] => jobName
                    // [1] => taskName
                    // [2] => processIndex
                    // [3] => threadNum
                    // [4] => inboundStr
                    // [5] => outboundStr
                    threadNum -= Integer.parseInt(task.split("#", -1)[3]);
                    zkConn.delete(workerPath + "/" + task);
                }
            }
            zkConn.set(workerPath, Integer.toString(threadNum));
        }

        // delete streams
        String streamPath = "/stream";
        List<String> streams = zkConn.getChildren(streamPath);
        for (String stream : streams) {
            if (stream.startsWith(jobName + "-")) {
                zkConn.deleteRecursive(streamPath + "/" + stream);
            }
        }

        zkConn.delete(jobPath);
    }

    public Map<String, String> getRunningTopologies() {
        List<String> jobNames = zkConn.getChildren("/master/job");
        Map<String, String> result = new HashMap<>();
        for (String name : jobNames) {
            result.put(name, zkConn.get("/master/job/" + name));
        }
        return result;
    }
}
