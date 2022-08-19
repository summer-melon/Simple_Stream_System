package com.toutiao.melon.workerprocess.job;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.toutiao.melon.workerprocess.thread.ComputedOutput;

@FunctionalInterface
public interface BeforeEmitCallback {

    ComputedOutput accept(DynamicMessage.Builder msgBuilder, Descriptors.Descriptor msgDesc, String targetStreamId);
}
