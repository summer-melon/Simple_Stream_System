package com.toutiao.melon.api.stream;

import com.google.protobuf.Descriptors;
import com.toutiao.melon.api.message.DynamicSchema;
import com.toutiao.melon.api.message.MessageDefinition;
import com.toutiao.melon.api.utils.ApiUtil;
import com.toutiao.melon.api.utils.ConnectUtil;
import java.util.HashMap;
import java.util.Map;

public class OutGoingStream {
    private final String streamIdPrefix;
    private final Map<String, DynamicSchema> outGoingStreamSchemas = new HashMap<>();

    public OutGoingStream(String jobName, String taskName) {
        this.streamIdPrefix = ConnectUtil.connect(jobName, taskName);
    }

    public OutGoingStream addSchema(String streamId, Field... fields) {
        ApiUtil.validateId(streamId);
        MessageDefinition.Builder msgDefBuilder = MessageDefinition.newBuilder("EventData");

        for (Field f : fields) {
            msgDefBuilder.addField(f.getFieldType(), f.getFieldName());
        }
        DynamicSchema schema = null;
        try {
            schema = DynamicSchema.newBuilder()
                    .addMessageDefinition(msgDefBuilder.build())
                    .build();
        } catch (Descriptors.DescriptorValidationException ignored) {
            ignored.printStackTrace();
        }
        outGoingStreamSchemas.put(streamIdPrefix + "-" + streamId, schema);
        return this;
    }

    public Map<String, DynamicSchema> getOutGoingStreamSchemas() {
        return outGoingStreamSchemas;
    }
}
