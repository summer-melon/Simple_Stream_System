package com.toutiao.melon.master.controller;

import com.google.protobuf.ByteString;
import com.toutiao.melon.master.service.JarFileService;
import com.toutiao.melon.rpc.ProvideJarGrpc.ProvideJarImplBase;
import com.toutiao.melon.rpc.ProvideJarRequest;
import com.toutiao.melon.rpc.ProvideJarResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Singleton
public class ProvideJarController extends ProvideJarImplBase {

    @Inject
    private JarFileService jarService;

    @Override
    public void provideJar(ProvideJarRequest request, StreamObserver<ProvideJarResponse> responseObserver) {
        String jobName = request.getJobName();
        ProvideJarResponse.Builder responseBuilder = ProvideJarResponse.newBuilder();
        if (!jarService.isJarFileExists(jobName)) {
            responseBuilder.setMessage("Jar file doesn't exist");
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            return;
        }

        InputStream jarInputStream = null;
        int len;
        byte[] buf = new byte[64 * 1024];
        try {
            jarInputStream = jarService.getInputStream(jobName);
            while ((len = jarInputStream.read(buf)) != -1) {
                responseBuilder.clear();
                responseBuilder.setJarBytes(ByteString.copyFrom(buf, 0, len));
                responseObserver.onNext(responseBuilder.build());
            }
            responseObserver.onCompleted();
        } catch (IOException e) {
            log.error(e.toString());
        } finally {
            if (jarInputStream != null) {
                try {
                    jarInputStream.close();
                } catch (IOException e) {
                    log.error(e.toString());
                }
            }
        }
    }
}
