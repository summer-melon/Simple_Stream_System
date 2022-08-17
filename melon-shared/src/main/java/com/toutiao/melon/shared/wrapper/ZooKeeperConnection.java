package com.toutiao.melon.shared.wrapper;

import java.util.List;
import org.apache.zookeeper.AddWatchMode;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Transaction;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.common.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ZooKeeperConnection {

    private static final Logger log = LoggerFactory.getLogger(ZooKeeperConnection.class);

    private ZooKeeper zk;

    public ZooKeeperConnection(ZooKeeper zk) {
        this.zk = zk;
    }

    public String get(String path) {
        try {
            return new String(zk.getData(path, null, null));
        } catch (KeeperException | InterruptedException e) {
            log.error("ZooKeeper get path failed, {}", e.toString());
        }
        return null;
    }

    public byte[] getBytesAndWatch(String path, Watcher watcher) {
        try {
            return zk.getData(path, watcher, null);
        } catch (KeeperException | InterruptedException e) {
            log.error("ZooKeeper get data failed: {}", e.toString());
        }
        return null;
    }

    public void set(String path, String data) {
        byte[] dataByte = null;
        if (data != null) {
            dataByte = data.getBytes();
        }
        try {
            zk.setData(path, dataByte, -1);
        } catch (KeeperException | InterruptedException e) {
            log.error("ZooKeeper set data failed: {}", e.toString());
        }
    }

    public boolean exists(String path) {
        try {
            return zk.exists(path, null) != null;
        } catch (KeeperException | InterruptedException e) {
            log.error("ZooKeeper set data failed: {}", e.toString());
        }
        return false;
    }

    /**
     * ZK 创建节点
     * @param path 节点路径
     * @param data 节点数据
     * @param createMode  节点类型
     */
    public boolean create(String path, String data, CreateMode createMode) {
        byte[] dataByte = null;
        if (data != null) {
            dataByte = data.getBytes();
        }
        try {
            zk.create(path, dataByte, ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
        } catch (KeeperException | InterruptedException e) {
            log.error("ZooKeeper create node fail: {}", e.toString());
            return false;
        }
        return true;
    }

    /**
     * 默认创建永久节点
     */
    public boolean create(String path, String data) {
        return create(path, data, CreateMode.PERSISTENT);
    }

    public List<String> getChildren(String path) {
        try {
            return zk.getChildren(path, false);
        } catch (KeeperException | InterruptedException e) {
            log.error("ZooKeeper get children fail, {}", e.toString());
        }
        return null;
    }

    /**
     * 删除路径节点
     */
    public void delete(String path) {
        try {
            zk.delete(path, -1);
        } catch (InterruptedException | KeeperException e) {
            log.error("ZooKeeper delete node fail, {}", e.toString());
        }
    }

    /**
     * 递归删除节点
     */
    public void deleteRecursive(String path) {
        PathUtils.validatePath(path);
        List<String> children = getChildren(path);
        if (!children.isEmpty()) {
            for (String child : children) {
                deleteRecursive(path + "/" + child);
            }
        }
        // 删除子节点
        delete(path);
    }

    /**
     * 添加监听器
     * @param path    节点路径
     * @param watcher 监听器
     */
    public void addWatch(String path, Watcher watcher) {
        try {
            zk.addWatch(path, watcher, AddWatchMode.PERSISTENT);
        } catch (KeeperException | InterruptedException e) {
            log.error("ZooKeeper add watcher fail, {}", e.toString());
        }
    }

    public Transaction transaction() {
        return zk.transaction();
    }

    public void close() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            log.error("{}", e.toString());
        }
    }
}
