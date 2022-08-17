package com.toutiao.melon.shared.provider;

import com.toutiao.melon.shared.wrapper.ZooKeeperConnection;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Provider;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZooKeeper建立连接
 */
public class ZooKeeperConnectionProvider implements Provider<ZooKeeperConnection> {

    private static final Logger log = LoggerFactory.getLogger(ZooKeeperConnectionProvider.class);

    @Inject
    private Properties prop;

    @Override
    public ZooKeeperConnection get() {
        String servers = prop.getProperty("melon.zookeeper.connect_server");
        int sessionTimeout = Integer.parseInt(prop.getProperty("melon.zookeeper.session_timeout"));
        int connectTimeout = Integer.parseInt(prop.getProperty("melon.zookeeper.connect_timeout"));

        ZooKeeper zk = null;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            zk = new ZooKeeper(servers, sessionTimeout, watchedEvent -> {
                switch (watchedEvent.getState()) {
                    case SyncConnected:
                        countDownLatch.countDown();
                        break;
                    case Expired:
                        log.error("ZooKeeper session expired.");
                        break;
                    default:
                        log.error("ZooKeeper Unknown State.");
                }
            });
            countDownLatch.await(connectTimeout, TimeUnit.MILLISECONDS);
        } catch (IOException | InterruptedException e) {
            log.error("{}", e.toString());
            System.exit(-1);
        }
        if (countDownLatch.getCount() != 0) {
            log.error("ZooKeeper connect timeout.");
            System.exit(-1);
        }
        return new ZooKeeperConnection(zk);
    }
}
