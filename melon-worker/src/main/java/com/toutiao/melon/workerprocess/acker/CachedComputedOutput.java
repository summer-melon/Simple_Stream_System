package com.toutiao.melon.workerprocess.acker;

import com.toutiao.melon.workerprocess.thread.ComputedOutput;

public class CachedComputedOutput {
    private int initTraceId;
    private String threadId;
    private ComputedOutput computedOutput;

    public CachedComputedOutput() {
    }

    public CachedComputedOutput(int initTraceId, String threadId, ComputedOutput computedOutput) {
        this.initTraceId = initTraceId;
        this.threadId = threadId;
        this.computedOutput = computedOutput;
    }

    public int getInitTraceId() {
        return initTraceId;
    }

    public void setInitTraceId(int initTraceId) {
        this.initTraceId = initTraceId;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public ComputedOutput getComputedOutput() {
        return computedOutput;
    }

    public void setComputedOutput(ComputedOutput computedOutput) {
        this.computedOutput = computedOutput;
    }
}
