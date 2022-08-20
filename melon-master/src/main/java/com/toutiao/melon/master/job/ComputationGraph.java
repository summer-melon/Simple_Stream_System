package com.toutiao.melon.master.job;

import java.util.List;
import java.util.Map;

public class ComputationGraph {
    // nodeId => TaskDefinition
    private Map<String, TaskDefinition> tasks;
    // Refer to JobLoader#getAssignOrder
    private List<String> assignOrder;

    public int getTotalThreads() {
        return tasks.values().stream()
                .mapToInt(t -> t.getProcessNum() * t.getThreadsPerProcess())
                .sum();
    }


    public ComputationGraph(Map<String, TaskDefinition> tasks, List<String> assignOrder) {
        this.tasks = tasks;
        this.assignOrder = assignOrder;
    }

    public Map<String, TaskDefinition> getTasks() {
        return tasks;
    }

    public void setTasks(Map<String, TaskDefinition> tasks) {
        this.tasks = tasks;
    }

    public List<String> getAssignOrder() {
        return assignOrder;
    }

    public void setAssignOrder(List<String> assignOrder) {
        this.assignOrder = assignOrder;
    }
}
