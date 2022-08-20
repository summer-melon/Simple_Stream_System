package com.toutiao.melon.workerprocess.job;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.DynamicMessage;
import com.toutiao.melon.api.message.DynamicSchema;
import com.toutiao.melon.api.stream.Collector;
import com.toutiao.melon.api.stream.Value;
import com.toutiao.melon.workerprocess.thread.ComputedOutput;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputCollectorImpl implements Collector {

    private static final Logger log = LoggerFactory.getLogger(OutputCollectorImpl.class);
    private final Map<String, DynamicSchema> outboundSchemaMap;
    private final BlockingQueue<ComputedOutput> outboundQueue;
    private final BeforeEmitCallback beforeEmit;
    private String streamIdPrefix = "";

    public OutputCollectorImpl(Map<String, DynamicSchema> outboundSchemaMap,
                               BlockingQueue<ComputedOutput> outboundQueue,
                               BeforeEmitCallback beforeEmit) {
        this.outboundSchemaMap = outboundSchemaMap;
        if (!outboundSchemaMap.isEmpty()) {
            Iterator<String> streamIdIter = outboundSchemaMap.keySet().iterator();
            try {
                while (streamIdPrefix.isEmpty()
                        || streamIdPrefix.contains("~")) { // for ~ackerInbound
                    String[] slicedStreamId = streamIdIter.next().split("-");
                    streamIdPrefix = slicedStreamId[0] + "-" + slicedStreamId[1] + "-";
                }
            } catch (NoSuchElementException ignored) {
                streamIdPrefix = "";
            }
        }
        this.outboundQueue = outboundQueue;
        this.beforeEmit = beforeEmit;
    }

    @Override
    public void emit(String targetStream, Value... tupleElements) {
        targetStream = streamIdPrefix + targetStream;
        DynamicSchema schema = outboundSchemaMap.get(targetStream);
        if (schema == null) {
            throw new RuntimeException("No such schema: " + targetStream);
        }
        DynamicMessage.Builder msgBuilder = schema.newMessageBuilder("TupleData");
        Descriptor msgDesc = msgBuilder.getDescriptorForType();
        for (Value v : tupleElements) {
            msgBuilder.setField(msgDesc.findFieldByName(v.getName()), v.getValue());
        }
        ComputedOutput output = beforeEmit.accept(msgBuilder, msgDesc, targetStream);
        try {
            outboundQueue.put(output);
        } catch (InterruptedException e) {
            log.error(e.toString());
        }
    }
}
