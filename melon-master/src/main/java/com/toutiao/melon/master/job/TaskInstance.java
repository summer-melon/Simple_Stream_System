package com.toutiao.melon.master.job;

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
}
