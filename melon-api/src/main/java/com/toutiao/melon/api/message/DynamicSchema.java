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

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.DynamicMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DynamicSchema {

    /**
     * Creates a new dynamic schema builder
     *
     * @return the schema builder
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Parses a serialized schema descriptor (from input stream; closes the stream)
     *
     * @param schemaDescIn the descriptor input stream
     * @return the schema object
     */
    public static DynamicSchema parseFrom(InputStream schemaDescIn)
            throws DescriptorValidationException, IOException {
        try {
            int len;
            byte[] buf = new byte[4096];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((len = schemaDescIn.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            return parseFrom(baos.toByteArray());
        } finally {
            schemaDescIn.close();
        }
    }

    /**
     * Parses a serialized schema descriptor (from byte array)
     *
     * @param schemaDescBuf the descriptor byte array
     * @return the schema object
     */
    public static DynamicSchema parseFrom(byte[] schemaDescBuf)
            throws DescriptorValidationException, IOException {
        return new DynamicSchema(FileDescriptorSet.parseFrom(schemaDescBuf));
    }

    /**
     * Creates a new dynamic message builder for the given message type
     *
     * @param msgTypeName the message type name
     * @return the message builder (null if not found)
     */
    public DynamicMessage.Builder newMessageBuilder(String msgTypeName) {
        Descriptor msgType = getMessageDescriptor(msgTypeName);
        if (msgType == null) {
            return null;
        }
        return DynamicMessage.newBuilder(msgType);
    }

    /**
     * Gets the protobuf message descriptor for the given message type
     *
     * @param msgTypeName the message type name
     * @return the message descriptor (null if not found)
     */
    public Descriptor getMessageDescriptor(String msgTypeName) {
        Descriptor msgType = msgDescriptorMapShort.get(msgTypeName);
        if (msgType == null) {
            msgType = msgDescriptorMapFull.get(msgTypeName);
        }
        return msgType;
    }

    /**
     * Returns the message types registered with the schema
     *
     * @return the set of message type names
     */
    public Set<String> getMessageTypes() {
        return new TreeSet<>(msgDescriptorMapFull.keySet());
    }

    /**
     * Serializes the schema
     *
     * @return the serialized schema descriptor
     */
    public byte[] toByteArray() {
        return fileDescSet.toByteArray();
    }

    /**
     * Returns a string representation of the schema
     *
     * @return the schema string
     */
    public String toString() {
        Set<String> msgTypes = getMessageTypes();
        return "types: " + msgTypes + "\n" + fileDescSet;
    }

    private DynamicSchema(FileDescriptorSet fileDescSet) throws DescriptorValidationException {
        this.fileDescSet = fileDescSet;
        Map<String, FileDescriptor> fileDescMap = init(fileDescSet);

        Set<String> msgDupes = new HashSet<>();
        for (FileDescriptor fileDesc : fileDescMap.values()) {
            for (Descriptor msgType : fileDesc.getMessageTypes()) {
                addMessageType(msgType, null, msgDupes);
            }
        }

        for (String msgName : msgDupes) {
            msgDescriptorMapShort.remove(msgName);
        }
    }

    private Map<String, FileDescriptor> init(FileDescriptorSet fileDescSet)
            throws DescriptorValidationException {
        // check for dupes
        Set<String> allFdProtoNames = new HashSet<>();
        for (FileDescriptorProto fdProto : fileDescSet.getFileList()) {
            if (allFdProtoNames.contains(fdProto.getName())) {
                throw new IllegalArgumentException("duplicate name: " + fdProto.getName());
            }
            allFdProtoNames.add(fdProto.getName());
        }

        // build FileDescriptors, resolve dependencies (imports) if any
        Map<String, FileDescriptor> resolvedFileDescMap = new HashMap<>();
        while (resolvedFileDescMap.size() < fileDescSet.getFileCount()) {
            for (FileDescriptorProto fdProto : fileDescSet.getFileList()) {
                if (resolvedFileDescMap.containsKey(fdProto.getName())) {
                    continue;
                }
                List<String> dependencyList = fdProto.getDependencyList();
                List<FileDescriptor> resolvedFdList = new ArrayList<>();
                for (String depName : dependencyList) {
                    if (!allFdProtoNames.contains(depName)) {
                        throw new IllegalArgumentException(
                                "cannot resolve import " + depName + " in " + fdProto.getName());
                    }
                    FileDescriptor fd = resolvedFileDescMap.get(depName);
                    if (fd != null) {
                        resolvedFdList.add(fd);
                    }
                }

                if (resolvedFdList.size() == dependencyList.size()) { // dependencies resolved
                    FileDescriptor[] fds = new FileDescriptor[resolvedFdList.size()];
                    FileDescriptor fd = FileDescriptor.buildFrom(
                            fdProto, resolvedFdList.toArray(fds));
                    resolvedFileDescMap.put(fdProto.getName(), fd);
                }
            }
        }

        return resolvedFileDescMap;
    }

    private void addMessageType(Descriptor msgType, String scope, Set<String> msgDupes) {
        String msgTypeNameFull = msgType.getFullName();
        String msgTypeNameShort = 
                (scope == null ? msgType.getName() : scope + "." + msgType.getName());

        if (msgDescriptorMapFull.containsKey(msgTypeNameFull)) {
            throw new IllegalArgumentException("duplicate name: " + msgTypeNameFull);
        }
        if (msgDescriptorMapShort.containsKey(msgTypeNameShort)) {
            msgDupes.add(msgTypeNameShort);
        }

        msgDescriptorMapFull.put(msgTypeNameFull, msgType);
        msgDescriptorMapShort.put(msgTypeNameShort, msgType);

        for (Descriptor nestedType : msgType.getNestedTypes()) {
            addMessageType(nestedType, msgTypeNameShort, msgDupes);
        }
    }

    private FileDescriptorSet fileDescSet;
    private Map<String, Descriptor> msgDescriptorMapFull = new HashMap<>();
    private Map<String, Descriptor> msgDescriptorMapShort = new HashMap<>();

    /**
     * DynamicSchema.Builder
     */
    public static class Builder {

        /**
         * Builds a dynamic schema
         *
         * @return the schema object
         */
        public DynamicSchema build() throws DescriptorValidationException {
            FileDescriptorSet.Builder fileDescSetBuilder = FileDescriptorSet.newBuilder();
            fileDescSetBuilder.addFile(fileDescProtoBuilder.build());
            return new DynamicSchema(fileDescSetBuilder.build());
        }

        public Builder setName(String name) {
            fileDescProtoBuilder.setName(name);
            return this;
        }

        public Builder setPackage(String name) {
            fileDescProtoBuilder.setPackage(name);
            return this;
        }

        public Builder addMessageDefinition(MessageDefinition msgDef) {
            fileDescProtoBuilder.addMessageType(msgDef.getMessageType());
            return this;
        }

        private Builder() {
            fileDescProtoBuilder = FileDescriptorProto.newBuilder();
        }

        private FileDescriptorProto.Builder fileDescProtoBuilder;
    }
}
