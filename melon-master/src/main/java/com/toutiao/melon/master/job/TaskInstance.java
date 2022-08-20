package com.toutiao.melon.master.job;

import java.util.Objects;

public class TaskInstance {
    private String nodeId;
    private int replicaId;

    public TaskInstance(String nodeId, int replicaId) {
        this.nodeId = nodeId;
        this.replicaId = replicaId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public int getReplicaId() {
        return replicaId;
    }

    public void setReplicaId(int replicaId) {
        this.replicaId = replicaId;
    }

    @Override
    public String toString() {
        return "TaskInstance{"
                + "nodeId='" + nodeId + '\''
                + ", replicaId=" + replicaId
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskInstance that = (TaskInstance) o;
        return replicaId == that.replicaId && Objects.equals(nodeId, that.nodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, replicaId);
    }
}
