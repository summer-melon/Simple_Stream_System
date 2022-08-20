package com.toutiao.melon.workerprocess.thread;


public class ComputedOutput {

    private String streamId;
    private byte[] bytes;

    public ComputedOutput() {
    }

    public ComputedOutput(String streamId, byte[] bytes) {
        this.streamId = streamId;
        this.bytes = bytes;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
