package com.toutiao.melon.api.stream;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.DynamicMessage;

/**
 * 数据流传递基础数据类型
 */
public class Event {
    private final DynamicMessage message;
    private final Descriptor messageDescriptor;

    public Event(DynamicMessage message, Descriptor messageDescriptor) {
        this.message = message;
        this.messageDescriptor = messageDescriptor;
    }

    public String getStringByName(String fieldName) {
        return (String) getFieldByName(fieldName);
    }

    public int getIntByName(String fieldName) {
        return (int) getFieldByName(fieldName);
    }

    public long getLongByName(String fieldName) {
        return (long) getFieldByName(fieldName);
    }

    private Object getFieldByName(String fieldName) {
        return message.getField(messageDescriptor.findFieldByName(fieldName));
    }
}
