package com.toutiao.melon.shared.wrapper;

import org.apache.zookeeper.ZooKeeper;

public class ZooKeeperConnection {

    private ZooKeeper zk;

    public ZooKeeperConnection(ZooKeeper zk) {
        this.zk = zk;
    }
}
