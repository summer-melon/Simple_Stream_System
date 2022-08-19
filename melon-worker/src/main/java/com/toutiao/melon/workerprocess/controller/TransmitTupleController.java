package com.toutiao.melon.workerprocess.controller;

import com.google.protobuf.Empty;
import com.toutiao.melon.rpc.RpcEvent;
import com.toutiao.melon.rpc.TransmitEventGrpc.TransmitEventImplBase;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Singleton
public class TransmitTupleController extends TransmitEventImplBase {

    private static final Empty EMPTY_MESSAGE = Empty.newBuilder().build();

    private final BlockingQueue<byte[]> inboundQueue = new LinkedBlockingQueue<>();

    public BlockingQueue<byte[]> getInboundQueue() {
        return inboundQueue;
    }

    @Override
    public void transmitEvent(RpcEvent request, StreamObserver<Empty> responseObserver) {
        try {
            inboundQueue.put(request.getEventBytes().toByteArray());
        } catch (InterruptedException e) {
            log.error("Failed to receive tuple: " + e.toString());
        }
        responseObserver.onNext(EMPTY_MESSAGE);
        responseObserver.onCompleted();
    }
}
