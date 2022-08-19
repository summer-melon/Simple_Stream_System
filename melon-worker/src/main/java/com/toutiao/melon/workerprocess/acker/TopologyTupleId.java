package com.toutiao.melon.workerprocess.acker;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopologyTupleId {
    private String topologyName;
    private int spoutTupleId;
}
