package com.toutiao.melon.workerprocess;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.protobuf.Descriptors;
import com.toutiao.melon.api.IOutStream;
import com.toutiao.melon.api.message.DynamicSchema;
import com.toutiao.melon.api.message.MessageDefinition;
import com.toutiao.melon.api.stream.FieldType;
import com.toutiao.melon.api.stream.OutGoingStream;
import com.toutiao.melon.shared.GuiceModule;
import com.toutiao.melon.shared.util.SharedUtil;
import com.toutiao.melon.shared.wrapper.ZooKeeperConnection;
import com.toutiao.melon.workerprocess.acker.Acker;
import com.toutiao.melon.workerprocess.controller.TransmitTupleController;
import com.toutiao.melon.workerprocess.job.OperatorLoader;
import com.toutiao.melon.workerprocess.thread.ComputeThread;
import com.toutiao.melon.workerprocess.thread.ComputedOutput;
import com.toutiao.melon.workerprocess.thread.TransmitTupleClientThread;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Transaction;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class WorkerProcessServer {

    private static final Logger log = LoggerFactory.getLogger(WorkerProcessServer.class);
    @Inject
    private TransmitTupleController messageReceiver;

    @Inject
    private TransmitTupleClientThread messageSender;

    @Inject
    private ZooKeeperConnection zkConn;



    private final ExecutorService threadPool = Executors.newCachedThreadPool(r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });

    private Server server;

    private int threadNum;
    private String topologyName;
    private String taskName;
    private String processIndex;
    private String inbound;

    public void start(String jarPath, String taskFullName) throws Throwable {
        decodeTaskFullName(taskFullName);
        boolean isAcker = "~acker".equals(taskName);

        Class<? extends IOutStream> opClass;
        if (isAcker) {
            opClass = Acker.class;
        } else {
            URL jarUrl = Paths.get(jarPath).toUri().toURL();
            opClass = new OperatorLoader().load(jarUrl, taskName);
        }

        // acker schema
        DynamicSchema ackerSchema = null;
        MessageDefinition msgDef = MessageDefinition.newBuilder("TupleData")
                .addField(FieldType.STRING, "_topologyName")
                .addField(FieldType.INT, "_spoutTupleId")
                .addField(FieldType.INT, "_traceId")
                .build();
        DynamicSchema.Builder schemaBuilder = DynamicSchema.newBuilder();
        schemaBuilder.addMessageDefinition(msgDef);
        try {
            ackerSchema = schemaBuilder.build();
        } catch (Descriptors.DescriptorValidationException e) {
            log.error(e.toString());
            System.exit(-1);
        }
        Map<String, DynamicSchema> outboundSchemaMap =
                registerOutboundSchemas(opClass, ackerSchema);

        BlockingQueue<byte[]> inboundQueue = messageReceiver.getInboundQueue();
        int port = startGrpcServer();
        registerInboundStream(port);

        messageSender.init(outboundSchemaMap.keySet());
        BlockingQueue<ComputedOutput> outboundQueue = messageSender.getOutboundQueue();

        DynamicSchema inboundSchema;
        if ("~acker".equals(taskName)) {
            inboundSchema = ackerSchema;
        } else {
            inboundSchema = blockUntilInboundSchemaAvailable();
        }
        threadPool.submit(messageSender);
        IOutStream op = opClass.newInstance();
        for (int i = 0; i < threadNum; ++i) {
            threadPool.submit(new ComputeThread(taskFullName + i,
                    topologyName, op, inboundSchema, outboundSchemaMap,
                    inboundQueue, outboundQueue, ackerSchema));
        }
        // TODO: add thread monitor as new thread
    }

    private static class BytesWrapper {
        private byte[] bytes;

        public BytesWrapper() {
        }

        public BytesWrapper(byte[] bytes) {
            this.bytes = bytes;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }
    }

    private DynamicSchema blockUntilInboundSchemaAvailable() throws Throwable {
        if (inbound.isEmpty()) {
            return null;
        }
        BytesWrapper wrapper = new BytesWrapper();
        CountDownLatch barrier = new CountDownLatch(1);
        String inboundPath = "/stream/" + inbound;
        wrapper.setBytes(zkConn.getBytesAndWatch(inboundPath, e -> {
            if (e.getType() == Watcher.Event.EventType.NodeDataChanged) {
                wrapper.setBytes(zkConn.getBytesAndWatch(inboundPath, null));
                barrier.countDown();
            }
        }));

        if (wrapper.getBytes() == null) {
            barrier.await();
        }
        return DynamicSchema.parseFrom(wrapper.getBytes());
    }

    private Map<String, DynamicSchema> registerOutboundSchemas(
            Class<? extends IOutStream> opClass, DynamicSchema ackerSchema)
            throws Throwable {
        IOutStream operator = opClass.newInstance();
        OutGoingStream declarer = new OutGoingStream(topologyName, taskName);
        operator.defineOutGoingStream(declarer);
        Map<String, DynamicSchema> outboundSchemaMap = declarer.getOutGoingStreamSchemas();
        Transaction txn = zkConn.transaction();
        for (Map.Entry<String, DynamicSchema> e : outboundSchemaMap.entrySet()) {
            String outboundPath = "/stream/" + e.getKey();
            zkConn.create(outboundPath, null);
            txn.setData(outboundPath, e.getValue().toByteArray(), -1);
        }
        if (!outboundSchemaMap.isEmpty()) {
            txn.commit();
        }

        String ackerStreamId = topologyName + "-~ackerInbound";
        outboundSchemaMap.put(ackerStreamId, ackerSchema);
        zkConn.create("/stream/" + ackerStreamId, null);
        return outboundSchemaMap;
    }

    private void registerInboundStream(int port) {
        String inboundPath = "/stream/" + inbound;
        zkConn.create(inboundPath, null);
        String ip = null;
        try {
            ip = SharedUtil.getHost();
        } catch (IOException e) {
            log.error("Cannot get host IP: " + e.toString());
            System.exit(-1);
        }
        zkConn.create(inboundPath + "/" + ip + ":" + port, null, CreateMode.EPHEMERAL);
    }

    private void decodeTaskFullName(String taskFullName) {
        // [0] => topologyName
        // [1] => taskName
        // [2] => processIndex
        // [3] => threadNum
        // [4] => inboundStr
        // [5] => outboundStr
        String[] taskInfo = taskFullName.split("#", -1);
        topologyName = taskInfo[0];
        taskName = taskInfo[1];
        processIndex = taskInfo[2];
        threadNum = Integer.parseInt(taskInfo[3]);
        // Only one inbound stream is allowed for every bolt
        inbound = taskInfo[4].split(";")[0];
    }

    private int startGrpcServer() throws IOException {
        server = ServerBuilder.forPort(0)
                .intercept(new ServerInterceptor() {
                    @Override
                    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
                            ServerCall<ReqT, RespT> call, Metadata headers,
                            ServerCallHandler<ReqT, RespT> next) {
                        call.setCompression("gzip");
                        return next.startCall(call, headers);
                    }
                })
                .addService(messageReceiver)
                .build()
                .start();
        log.info("Server started, listening on tcp port " + server.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                WorkerProcessServer.this.stop();
            } catch (InterruptedException e) {
                log.error(e.toString());
            }
            cleanUpSingletonResources();
            log.info("Server shut down");
        }));
        return server.getPort();
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            cleanUpSingletonResources();
            log.info("Server shut down");
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
            cleanUpSingletonResources();
            log.info("Server shut down");
        }
    }

    private void cleanUpSingletonResources() {
        log.info("Cleaning up global resources");
        Injector injector = Guice.createInjector(new GuiceModule());

        injector.getInstance(ZooKeeperConnection.class).close();
    }
}
