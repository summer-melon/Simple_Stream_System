package com.toutiao.melon.master.job;

import com.toutiao.melon.api.job.Edge;
import com.toutiao.melon.api.job.Node;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
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
}
