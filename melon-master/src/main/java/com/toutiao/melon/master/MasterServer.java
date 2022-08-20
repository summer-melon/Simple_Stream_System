package com.toutiao.melon.master;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.toutiao.melon.master.controller.ManageJobController;
import com.toutiao.melon.master.controller.ProvideJarController;
import com.toutiao.melon.shared.GuiceModule;
import io.grpc.BindableService;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MasterServer {

    private Server server;
    private int port;
    private static final Logger log = LoggerFactory.getLogger(MasterServer.class);

    public MasterServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        Injector injector = Guice.createInjector(new GuiceModule());

        server = ServerBuilder.forPort(port)
                .intercept(new ServerInterceptor() {
                    @Override
                    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
                            ServerCall<ReqT, RespT> call, Metadata headers,
                            ServerCallHandler<ReqT, RespT> next) {
                        call.setCompression("gzip");
                        return next.startCall(call, headers);
                    }
                })
                .addService(injector.getInstance(ManageJobController.class))
                .addService(injector.getInstance(ProvideJarController.class))
                .build()
                .start();

        log.info("Server started, listening on tcp port " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                MasterServer.this.stop();
            } catch (InterruptedException e) {
                log.error(e.toString());
            }
            log.info("Server shut down");
        }));
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            log.info("Server shut down");
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
            log.info("Server shut down");
        }
    }
}
