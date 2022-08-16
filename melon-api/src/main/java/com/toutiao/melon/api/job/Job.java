package com.toutiao.melon.api.job;

import com.toutiao.melon.api.IOperator;
import com.toutiao.melon.api.ISource;
import com.toutiao.melon.api.utils.ApiUtil;
import com.toutiao.melon.api.utils.EdgeConnectUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业拓扑定义
 */
public class Job {

    /**
     * [operatorId, Node{className, true/false, process, thread}]
     */
    private Map<String, Node> nodes;

    /**
     * [operatorId, Edge{targetId, streamId}]
     */
    private Map<String, List<Edge>> edges;

    private Job(Map<String, Node> nodes, Map<String, List<Edge>> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String sourceId;

        private Map<String, Node> nodes = new HashMap<>();

        private Map<String, List<Edge>> edges = new HashMap<>();

        private Builder() {

        }

        public Job.Builder addSource(String sourceId, Class<? extends ISource> sourceClass,
                                     int processNum, int threadNum) {
            ApiUtil.validateId(sourceId);
            if (sourceId != null && !"".equals(sourceId)) {
                nodes.remove(sourceId);
            }
            this.sourceId = sourceId;
            nodes.put(sourceId, new Node(sourceClass.getCanonicalName(),
                    true, processNum, threadNum));
            return this;
        }

        public Job.Builder addOperator(String operatorId, Class<? extends IOperator> operatorClass,
                                       int processNum, int threadNum) {
            ApiUtil.validateId(operatorId);
            if (sourceId != null && sourceId.equals(operatorId)) {
                throw new IllegalArgumentException("operatorId shouldn't be same as sourceId");
            }
            nodes.put(operatorId, new Node(operatorClass.getCanonicalName(),
                    false, processNum, threadNum));
            return this;
        }

        public Job.Builder addStream(String sourceId, String targetId, String streamId) {
            ApiUtil.validateId(sourceId);
            ApiUtil.validateId(targetId);
            ApiUtil.validateId(streamId);
            List<Edge> lists = this.edges.getOrDefault(sourceId, new ArrayList<>());
            lists.add(new Edge(targetId, EdgeConnectUtil.connect(sourceId, streamId)));
            edges.put(sourceId, lists);
            return this;
        }

        public Job build() {
            if (sourceId == null) {
                throw new RuntimeException("Missing DataSource");
            }
            return new Job(nodes, edges);
        }
    }
}
