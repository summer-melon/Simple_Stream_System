package com.toutiao.melon.workerprocess.thread;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.toutiao.melon.api.IOperator;
import com.toutiao.melon.api.IOutStream;
import com.toutiao.melon.api.ISource;
import com.toutiao.melon.api.message.DynamicSchema;
import com.toutiao.melon.api.stream.Collector;
import com.toutiao.melon.api.stream.Event;
import com.toutiao.melon.workerprocess.job.OutputCollectorImpl;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComputeThread implements Runnable {

    private final String threadId;
    private final String jobName;
    private final IOutStream ioutStream;
    private final DynamicSchema inboundSchema;
    private final DynamicSchema ackerSchema;
    private final BlockingQueue<byte[]> inboundQueue;
    private final Map<String, DynamicSchema> outboundSchemaMap;
    private final BlockingQueue<ComputedOutput> outboundQueue;

    public ComputeThread(String threadId,
                         String jobName,
                         IOutStream ioutStream,
                         DynamicSchema inboundSchema,
                         Map<String, DynamicSchema> outboundSchemaMap,
                         BlockingQueue<byte[]> inboundQueue,
                         BlockingQueue<ComputedOutput> outboundQueue,
                         DynamicSchema ackerSchema) {
        this.ioutStream = ioutStream;
        this.threadId = threadId;
        this.jobName = jobName;
        this.inboundSchema = inboundSchema;
        this.outboundSchemaMap = outboundSchemaMap;
        this.inboundQueue = inboundQueue;
        this.outboundQueue = outboundQueue;
        this.ackerSchema = ackerSchema;
    }

    // TODO: check if process will exit if this thread
    //       throws an exception
    @Override
    public void run() {
        if (ioutStream instanceof IOperator) {
            boltLoop();
        } else { // ISpout
            spoutLoop();
        }
    }


    private void spoutLoop() {
        ISource spout = (ISource) ioutStream;
        Collector outputCollector = new OutputCollectorImpl(
                this.outboundSchemaMap,
                this.outboundQueue,
                (msgBuilder, msgDesc, targetStreamId) -> {
                    int spoutTupleId = ThreadLocalRandom.current().nextInt();
                    int traceId = ThreadLocalRandom.current().nextInt();
                    msgBuilder.setField(msgDesc.findFieldByName("_jobName"), jobName);
                    msgBuilder.setField(msgDesc.findFieldByName("_spoutTupleId"), spoutTupleId);
                    msgBuilder.setField(msgDesc.findFieldByName("_traceId"), traceId);

                    ComputedOutput output = new ComputedOutput(targetStreamId, msgBuilder.build().toByteArray());
                    return output;
                });

        while (true) {
            try {
                // TODO: add max tuple num constraint
                spout.getEvents(outputCollector);
            } catch (Throwable t) {
                t.printStackTrace();
                log.error(t.toString());
            }
        }
    }

    @Data
    @NoArgsConstructor
    private static class Wrapper<T> {
        T value;
    }

    private void boltLoop() {
        IOperator bolt = (IOperator) ioutStream;
        Wrapper<Integer> spoutTupleId = new Wrapper<>();
        Collector outputCollector = new OutputCollectorImpl(
                this.outboundSchemaMap,
                this.outboundQueue,
                (msgBuilder, msgDesc, targetStreamId) -> {
                    int prevSpoutTupleId = spoutTupleId.getValue();
                    int traceId = ThreadLocalRandom.current().nextInt();
                    msgBuilder.setField(msgDesc.findFieldByName("jobName"), jobName);
                    msgBuilder.setField(msgDesc.findFieldByName("_spoutTupleId"), prevSpoutTupleId);
                    msgBuilder.setField(msgDesc.findFieldByName("_traceId"), traceId);
                    return new ComputedOutput(targetStreamId, msgBuilder.build().toByteArray());
                });

        while (true) {
            Event event;
            try {
                event = decodeInboundMessage();
                spoutTupleId.setValue(event.getIntByName("_spoutTupleId"));
                bolt.compute(event, outputCollector);
            } catch (Throwable t) {
                t.printStackTrace();
                log.error(t.toString());
            }
        }
    }


    private Event decodeInboundMessage()
            throws InterruptedException, InvalidProtocolBufferException {
        byte[] messageBytes = inboundQueue.take();
        DynamicMessage.Builder parsedMessageBuilder = inboundSchema.newMessageBuilder("TupleData");
        Descriptors.Descriptor parsedMsgDesc = parsedMessageBuilder.getDescriptorForType();
        DynamicMessage parsedMessage = parsedMessageBuilder.mergeFrom(messageBytes).build();
        return new Event(parsedMessage, parsedMsgDesc);
    }

}
