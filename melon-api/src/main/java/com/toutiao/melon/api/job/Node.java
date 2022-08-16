package com.toutiao.melon.api.job;

/**
 * 拓扑图节点
 */
public class Node {

    private String className;

    private boolean isSource;

    private int processNum;

    private int threadNum;

    public Node() {
    }

    public Node(String className, boolean isSource, int processNum, int threadNum) {
        this.className = className;
        this.isSource = isSource;
        this.processNum = processNum;
        this.threadNum = threadNum;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isSource() {
        return isSource;
    }

    public void setSource(boolean source) {
        isSource = source;
    }

    public int getProcessNum() {
        return processNum;
    }

    public void setProcessNum(int processNum) {
        this.processNum = processNum;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    @Override
    public String toString() {
        return "Node{"
                + "className='" + className + '\''
                + ", isSource=" + isSource
                + ", processNum=" + processNum
                + ", threadNum=" + threadNum
                + '}';
    }
}
