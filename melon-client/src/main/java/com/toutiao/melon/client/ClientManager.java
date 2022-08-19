package com.toutiao.melon.client;

import com.google.protobuf.ByteString;
import com.toutiao.melon.rpc.ManageJobGrpc;
import com.toutiao.melon.rpc.ManageJobGrpc.ManageJobStub;
import com.toutiao.melon.rpc.ManageJobRequest;
import com.toutiao.melon.rpc.ManageJobRequestMetadata;
import com.toutiao.melon.rpc.ManageJobRequestMetadata.RequestType;
import com.toutiao.melon.rpc.ManageJobResponse;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

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

    public String manageJob(RequestType requestType, String jobName, InputStream jarFileStream)
            throws InterruptedException, IOException {
        StreamObserver<ManageJobRequest> request;
        StringWrapper message = new StringWrapper();
        CountDownLatch responseReceived = new CountDownLatch(1);
        request = grpcStub
                .withCompression("gzip")
                .manageJob(new StreamObserver<ManageJobResponse>() {
                    @Override
                    public void onNext(ManageJobResponse manageJobResponse) {
                        message.setString(manageJobResponse.getMessage());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        message.setString("NetWork error: " + throwable.toString());
                        responseReceived.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        responseReceived.countDown();
                    }
                });

        ManageJobRequestMetadata.Builder metadataBuilder = ManageJobRequestMetadata.newBuilder()
                        .setRequestType(requestType);
        if (jobName != null) {
            metadataBuilder.setJobName(jobName);
        }
        ManageJobRequest.Builder clientRequestBuilder = ManageJobRequest.newBuilder()
                        .setMetadata(metadataBuilder.build());
        request.onNext(clientRequestBuilder.build());

        if (jarFileStream != null) {
            int len;
            byte[] buf = new byte[64 * 1024];
            while ((len = jarFileStream.read(buf)) != -1) {
                clientRequestBuilder.clear();
                clientRequestBuilder.setJarBytes(ByteString.copyFrom(buf, 0, len));
                request.onNext(clientRequestBuilder.build());
            }
        }

        request.onCompleted();
        responseReceived.await();
        return message.getString();
    }
}
