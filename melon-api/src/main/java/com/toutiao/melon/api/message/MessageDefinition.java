/*
 * Copyright 2015 protobuf-dynamic developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.toutiao.melon.api.message;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.toutiao.melon.api.stream.FieldType;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息定义
 */
public class MessageDefinition {

    private DescriptorProto messageType;

    private MessageDefinition(DescriptorProto msgType) {
        messageType = msgType;
    }

    public DescriptorProto getMessageType() {
        return messageType;
    }

    public String toString() {
        return messageType.toString();
    }

    public static Builder newBuilder(String msgTypeName) {
        return new Builder(msgTypeName);
    }

    public static class Builder {

        public Builder addField(FieldType type, String name) {
            FieldDescriptorProto.Builder fieldBuilder = FieldDescriptorProto.newBuilder();
            fieldBuilder.setLabel(FieldDescriptorProto.Label.LABEL_REQUIRED);
            FieldDescriptorProto.Type primType = typeMap.get(type);
            fieldBuilder.setType(primType);
            fieldBuilder.setName(name).setNumber(++messageNumber);
            messageTypeBuilder.addField(fieldBuilder.build());
            return this;
        }

        public MessageDefinition build() {
            return new MessageDefinition(messageTypeBuilder.build());
        }

        private Builder(String msgTypeName) {
            messageTypeBuilder = DescriptorProto.newBuilder();
            messageTypeBuilder.setName(msgTypeName);
        }

        private int messageNumber = 0;
        private DescriptorProto.Builder messageTypeBuilder;
    }

    private static Map<FieldType, FieldDescriptorProto.Type> typeMap;

    static {
        typeMap = new HashMap<>();
        typeMap.put(FieldType.STRING, FieldDescriptorProto.Type.TYPE_STRING);
        typeMap.put(FieldType.INT, FieldDescriptorProto.Type.TYPE_INT32);
        typeMap.put(FieldType.LONG, FieldDescriptorProto.Type.TYPE_INT64);
        // typeMap.put(FieldType.BOOLEAN, FieldDescriptorProto.Type.TYPE_BOOL);
        // typeMap.put(FieldType.FLOAT, FieldDescriptorProto.Type.TYPE_FLOAT);
        // typeMap.put(FieldType.DOUBLE, FieldDescriptorProto.Type.TYPE_DOUBLE);
        // typeMap.put(FieldType.BYTES, FieldDescriptorProto.Type.TYPE_BYTES);
    }
}
