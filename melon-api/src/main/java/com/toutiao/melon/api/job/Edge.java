package com.toutiao.melon.api.job;

/**
 * 拓扑图边
 */
public class Edge {

    /** 目标Node Id */
    private String targetId;

    /** 下游流Id */
    private String streamId;

    public Edge() {
    }

    public Edge(String targetId, String streamId) {
        this.targetId = targetId;
        this.streamId = streamId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    @Override
    public String toString() {
        return "Edge{"
                + "targetId='" + targetId + '\''
                + ", streamId='" + streamId + '\''
                + '}';
    }
}
