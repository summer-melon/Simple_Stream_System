package com.toutiao.melon.workerprocess.acker;

public class TopologyTupleId {
    private String topologyName;
    private int spoutTupleId;

    public TopologyTupleId() {
    }

    public TopologyTupleId(String topologyName, int spoutTupleId) {
        this.topologyName = topologyName;
        this.spoutTupleId = spoutTupleId;
    }

    public String getTopologyName() {
        return topologyName;
    }

    public void setTopologyName(String topologyName) {
        this.topologyName = topologyName;
    }

    public int getSpoutTupleId() {
        return spoutTupleId;
    }

    public void setSpoutTupleId(int spoutTupleId) {
        this.spoutTupleId = spoutTupleId;
    }

    @Override
    public String toString() {
        return "TopologyTupleId{"
                + "topologyName='" + topologyName + '\''
                + ", spoutTupleId=" + spoutTupleId
                + '}';
    }
}
