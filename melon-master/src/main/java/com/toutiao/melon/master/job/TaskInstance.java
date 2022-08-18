package com.toutiao.melon.master.job;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskInstance {
    private String nodeId;
    private int replicaId;
}
