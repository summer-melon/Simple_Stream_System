package com.toutiao.melon.master.job;

import com.toutiao.melon.api.job.Edge;
import com.toutiao.melon.api.job.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskDefinition {
    private int processNum;
    private int threadsPerProcess;
    private List<String> inboundStreamIds;
    private List<String> outboundStreamIds;

    public TaskDefinition() {
        inboundStreamIds = new ArrayList<>();
        outboundStreamIds = new ArrayList<>();
    }

    public TaskDefinition(Node node, List<Edge> outboundEdges) {
        processNum = node.getProcessNum();
        threadsPerProcess = node.getThreadNum();
        inboundStreamIds = new ArrayList<>();
        outboundStreamIds = outboundEdges.stream()
                .map(Edge::getStreamId)
                .collect(Collectors.toList());
    }

    public void addInboundStream(String inboundStreamId) {
        if (!inboundStreamIds.contains(inboundStreamId)) {
            inboundStreamIds.add(inboundStreamId);
        }
    }

    public int getProcessNum() {
        return processNum;
    }

    public void setProcessNum(int processNum) {
        this.processNum = processNum;
    }

    public int getThreadsPerProcess() {
        return threadsPerProcess;
    }

    public void setThreadsPerProcess(int threadsPerProcess) {
        this.threadsPerProcess = threadsPerProcess;
    }

    public List<String> getInboundStreamIds() {
        return inboundStreamIds;
    }

    public void setInboundStreamIds(List<String> inboundStreamIds) {
        this.inboundStreamIds = inboundStreamIds;
    }

    public List<String> getOutboundStreamIds() {
        return outboundStreamIds;
    }

    public void setOutboundStreamIds(List<String> outboundStreamIds) {
        this.outboundStreamIds = outboundStreamIds;
    }
}
