package com.toutiao.melon.api.stream;

import com.google.protobuf.Descriptors;
import com.toutiao.melon.api.message.DynamicSchema;
import com.toutiao.melon.api.message.MessageDefinition;
import com.toutiao.melon.api.utils.ApiUtil;

import java.util.HashMap;
import java.util.Map;

public class OutGoingStream {
    private final String streamIdPrefix;
    private final Map<String, DynamicSchema> OutGoingStreamSchemas = new HashMap<>();

    public OutGoingStream(String topologyName, String taskName) {
        this.streamIdPrefix = topologyName + "-" + taskName;
    }

    public OutGoingStream addSchema(String streamId, Field... fields) {
        ApiUtil.validateId(streamId);
        MessageDefinition.Builder msgDefBuilder = MessageDefinition.newBuilder("TupleData");

        // fields used by acker
        msgDefBuilder.addField(FieldType.STRING, "_topologyName");
        msgDefBuilder.addField(FieldType.INT, "_spoutTupleId");
        msgDefBuilder.addField(FieldType.INT, "_traceId");

        for (Field f : fields) {
            msgDefBuilder.addField(f.getFieldType(), f.getFieldName());
        }
        DynamicSchema schema = null;
        try {
            schema = DynamicSchema.newBuilder()
                    .addMessageDefinition(msgDefBuilder.build())
                    .build();
        } catch (Descriptors.DescriptorValidationException ignored) {
        }
        OutGoingStreamSchemas.put(streamIdPrefix + "-" + streamId, schema);
        return this;
    }

    public Map<String, DynamicSchema> getOutGoingStreamSchemas() {
        return OutGoingStreamSchemas;
    }
}
