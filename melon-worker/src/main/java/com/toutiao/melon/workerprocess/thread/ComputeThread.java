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
import com.toutiao.melon.workerprocess.acker.*;
import com.toutiao.melon.workerprocess.topology.OutputCollectorImpl;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class ComputeThread implements Runnable {

    private final String threadId;
    private final String topologyName;
    private final IOutStream ioutStream;
    private final DynamicSchema inboundSchema;
    private final DynamicSchema ackerSchema;
    private final BlockingQueue<byte[]> inboundQueue;
    private final Map<String, DynamicSchema> outboundSchemaMap;
    private final BlockingQueue<ComputedOutput> outboundQueue;

    public ComputeThread(String threadId,
                         String topologyName,
                         IOutStream ioutStream,
                         DynamicSchema inboundSchema,
                         Map<String, DynamicSchema> outboundSchemaMap,
                         BlockingQueue<byte[]> inboundQueue,
                         BlockingQueue<ComputedOutput> outboundQueue,
                         DynamicSchema ackerSchema) {
        this.ioutStream = ioutStream;
        this.threadId = threadId;
        this.topologyName = topologyName;
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
        if (ioutStream instanceof Acker) {
            ackerLoop();
        } else if (ioutStream instanceof IOperator) {
            boltLoop();
        } else { // ISpout
            RedisAsyncCommands<TopologyTupleId, CachedComputedOutput> tupleCacheCommands = registerReplay();
            spoutLoop(tupleCacheCommands);
        }
    }

    private RedisAsyncCommands<TopologyTupleId, CachedComputedOutput> registerReplay() {
        String tupleCacheUriStr = System.getProperty("stormy.redis.tuple_cache_uri");
        RedisClient tupleCacheClient = RedisClient.create(tupleCacheUriStr);
        StatefulRedisConnection<TopologyTupleId, CachedComputedOutput> cacheConn =
                tupleCacheClient.connect(new TupleCacheCodec());
        RedisAsyncCommands<TopologyTupleId, CachedComputedOutput> tupleCacheCommands = cacheConn.async();

        String traceRedisUriStr = System.getProperty("stormy.redis.trace_uri");
        RedisURI traceRedisUri = RedisURI.create(traceRedisUriStr);
        RedisClient traceClient = RedisClient.create(traceRedisUri);
        StatefulRedisPubSubConnection<String, TopologyTupleId> traceConn =
                traceClient.connectPubSub(new AckerPubSubCodec());
        traceConn.addListener(new RedisPubSubAdapter<String, TopologyTupleId>() {
            @Override
            public void message(String channel, TopologyTupleId key) {
                try {
                    tupleCacheCommands.expire(key, 20);
                    CachedComputedOutput cachedOutput = tupleCacheCommands.get(key).get();
                    if (cachedOutput != null && threadId.equals(cachedOutput.getThreadId())) {
                        ack(topologyName, key.getSpoutTupleId(), cachedOutput.getInitTraceId());
                        outboundQueue.put(cachedOutput.getComputedOutput());
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    log.error("Failed to replay tuple: " + t.toString());
                }
            }
        });
        RedisPubSubCommands<String, TopologyTupleId> sync = traceConn.sync();
        sync.configSet("notify-keyspace-events", "xE");
        sync.subscribe("__keyevent@" + traceRedisUri.getDatabase() + "__:expired");

        return tupleCacheCommands;
    }

    private void spoutLoop(RedisAsyncCommands<TopologyTupleId, CachedComputedOutput> tupleCacheCommands) {
        ISource spout = (ISource) ioutStream;
        Collector outputCollector = new OutputCollectorImpl(
                this.outboundSchemaMap,
                this.outboundQueue,
                (msgBuilder, msgDesc, targetStreamId) -> {
                    int spoutTupleId = ThreadLocalRandom.current().nextInt();
                    int traceId = ThreadLocalRandom.current().nextInt();
                    msgBuilder.setField(msgDesc.findFieldByName("_topologyName"), topologyName);
                    msgBuilder.setField(msgDesc.findFieldByName("_spoutTupleId"), spoutTupleId);
                    msgBuilder.setField(msgDesc.findFieldByName("_traceId"), traceId);

                    ComputedOutput output = new ComputedOutput(targetStreamId, msgBuilder.build().toByteArray());
                    TopologyTupleId topologyTupleId = new TopologyTupleId(topologyName, spoutTupleId);
                    tupleCacheCommands.set(topologyTupleId, new CachedComputedOutput(traceId, threadId, output));
                    // TODO: reconsider tuple cache time
                    tupleCacheCommands.expire(topologyTupleId, 20);
                    ack(topologyName, spoutTupleId, traceId);
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
                    msgBuilder.setField(msgDesc.findFieldByName("_topologyName"), topologyName);
                    msgBuilder.setField(msgDesc.findFieldByName("_spoutTupleId"), prevSpoutTupleId);
                    msgBuilder.setField(msgDesc.findFieldByName("_traceId"), traceId);
                    ack(topologyName, prevSpoutTupleId, traceId);
                    return new ComputedOutput(targetStreamId, msgBuilder.build().toByteArray());
                });

        while (true) {
            Event event;
            try {
                event = decodeInboundMessage();
                spoutTupleId.setValue(event.getIntByName("_spoutTupleId"));
                bolt.compute(event, outputCollector);
                ack(topologyName, spoutTupleId.getValue(), event.getIntByName("_traceId"));
            } catch (Throwable t) {
                t.printStackTrace();
                log.error(t.toString());
            }
        }
    }

    private void ackerLoop() {
        Acker acker = (Acker) ioutStream;
        while (true) {
            Event event;
            try {
                event = decodeInboundMessage();
                acker.compute(event, null);
            } catch (Throwable t) {
                t.printStackTrace();
                log.error(t.toString());
            }
        }
    }

    private Event decodeInboundMessage() throws InterruptedException, InvalidProtocolBufferException {
        byte[] messageBytes = inboundQueue.take();
        DynamicMessage.Builder parsedMessageBuilder = inboundSchema.newMessageBuilder("TupleData");
        Descriptors.Descriptor parsedMsgDesc = parsedMessageBuilder.getDescriptorForType();
        DynamicMessage parsedMessage = parsedMessageBuilder.mergeFrom(messageBytes).build();
        return new Event(parsedMessage, parsedMsgDesc);
    }

    private void ack(String topologyName, int spoutTupleId, int traceId) {
        try {
            DynamicMessage.Builder msgBuilder = ackerSchema.newMessageBuilder("TupleData");
            Descriptors.Descriptor msgDesc = msgBuilder.getDescriptorForType();
            msgBuilder.setField(msgDesc.findFieldByName("_topologyName"), topologyName);
            msgBuilder.setField(msgDesc.findFieldByName("_spoutTupleId"), spoutTupleId);
            msgBuilder.setField(msgDesc.findFieldByName("_traceId"), traceId);
            outboundQueue.put(new ComputedOutput(topologyName + "-~ackerInbound",
                    msgBuilder.build().toByteArray()));
        } catch (Throwable t) {
            t.printStackTrace();
            log.error("Failed to ack: " + t.toString());
            System.exit(-1);
        }
    }
}
