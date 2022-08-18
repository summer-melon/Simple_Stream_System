package com.toutiao.melon.workerprocess.acker;

import com.toutiao.melon.workerprocess.thread.ComputedOutput;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CachedComputedOutput {
    private int initTraceId;
    private String threadId;
    private ComputedOutput computedOutput;
}
