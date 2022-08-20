package com.toutiao.melon.workerprocess.thread;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.toutiao.melon.rpc.RpcEvent;
import com.toutiao.melon.rpc.TransmitEventGrpc;
import com.toutiao.melon.rpc.TransmitEventGrpc.TransmitEventStub;
import com.toutiao.melon.shared.wrapper.ZooKeeperConnection;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.Watcher;

@Slf4j
@Singleton
public class TransmitTupleClientThread implements Runnable {

    @Inject
    private ZooKeeperConnection zkConn;

    private final BlockingQueue<ComputedOutput> outboundQueue = new LinkedBlockingQueue<>();
    private final Map<String, Map<String, TransmitEventStub>> clients = new HashMap<>();
    private final Map<String, Lock> streamServerLocks = new HashMap<>();
    private final Map<String, Iterator<TransmitEventStub>> iterators = new HashMap<>();

    public void init(Set<String> outbounds) {
        for (String streamId : outbounds) {
            Map<String, TransmitEventStub> m = new HashMap<>();
            clients.put(streamId, m);
            iterators.put(streamId, m.values().iterator());
            streamServerLocks.put(streamId, new ReentrantLock());

            zkConn.addWatch("/stream/" + streamId, e -> {
                if (e.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    handleServerChange(streamId);
                }
            });
            handleServerChange(streamId);
        }
    }

    private void handleServerChange(String streamId) {
        try {
            streamServerLocks.get(streamId).lock();
            List<String> currentServers = zkConn.getChildren("/stream/" + streamId);
            Map<String, TransmitEventStub> clientGroup = clients.get(streamId);
            currentServers.forEach(s -> {
                if (!clientGroup.containsKey(s)) {
                    ManagedChannel channel = ManagedChannelBuilder.forTarget(s)
                            .usePlaintext()
                            .build();
                    clientGroup.put(s, TransmitEventGrpc.newStub(channel).withCompression("gzip"));
                }
            });
            Iterator<String> iter = clientGroup.keySet().iterator();
            while (iter.hasNext()) {
                String s = iter.next();
                if (!currentServers.contains(s)) {
                    ((ManagedChannel) clientGroup.get(s).getChannel()).shutdown();
                    iter.remove();
                }
            }
            iterators.put(streamId, clientGroup.values().iterator());
        } finally {
            streamServerLocks.get(streamId).unlock();
        }
    }

    public BlockingQueue<ComputedOutput> getOutboundQueue() {
        return outboundQueue;
    }

    @Override
    public void run() {
        while (true) {
            Lock lock = null;
            try {
                ComputedOutput output = outboundQueue.take();
                String streamId = output.getStreamId();
                lock = streamServerLocks.get(streamId);
                lock.lock();
                Iterator<TransmitEventStub> stubIter = iterators.get(streamId);
                if (!stubIter.hasNext()) {
                    Map<String, TransmitEventStub> clientGroup = clients.get(streamId);
                    if (clientGroup.isEmpty()) {
                        continue; // drop tuple if no stream target
                    }
                    stubIter = clientGroup.values().iterator();
                }
                TransmitEventStub stub = stubIter.next();
                iterators.put(streamId, stubIter);
                stub.transmitEvent(RpcEvent.newBuilder()
                        .setEventBytes(ByteString.copyFrom(output.getBytes()))
                        .build(), new StreamObserver<Empty>() {
                            @Override
                            public void onNext(Empty value) {
                            }

                            @Override
                            public void onError(Throwable t) {
                            }

                            @Override
                            public void onCompleted() {
                            }
                        });
            } catch (Throwable t) {
                log.error(t.toString());
            } finally {
                if (lock != null) {
                    lock.unlock();
                }
            }
        }
    }
}
