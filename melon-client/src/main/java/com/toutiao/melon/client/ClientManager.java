package com.toutiao.melon.client;

import com.toutiao.melon.rpc.ManageJobGrpc;
import com.toutiao.melon.rpc.ManageJobGrpc.ManageJobStub;
import io.grpc.Channel;

public class ClientManager {

    private final ManageJobStub grpcStub;

    public ClientManager(Channel channel) {
        this.grpcStub = ManageJobGrpc.newStub(channel);
    }

    private static class StringWrapper {
        private String string;

        public StringWrapper() {
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }
}
