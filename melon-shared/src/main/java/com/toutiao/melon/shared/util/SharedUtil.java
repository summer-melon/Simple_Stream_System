package com.toutiao.melon.shared.util;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.toutiao.melon.shared.GuiceModule;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharedUtil {

    private static final Logger log = LoggerFactory.getLogger(SharedUtil.class);

    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final int DEFAULT_PORT = 2181;

    public static String getHost() throws IOException {
        Injector injector = Guice.createInjector(new GuiceModule());
        Properties prop = injector.getInstance(Properties.class);

        String zookeeperServers = prop.getProperty("melon.zookeeper.connect_server");
        String[] serverList = zookeeperServers.split(COMMA);
        for (String server : serverList) {
            String[] hostAndPort;
            if (server.contains(COLON)) {
                hostAndPort = server.split(COLON);
            } else {
                hostAndPort = new String[]{server, String.valueOf(DEFAULT_PORT)};
            }

            try (Socket socket = new Socket(hostAndPort[0], Integer.parseInt(hostAndPort[1]))) {
                return socket.getLocalAddress().getHostAddress();
            } catch (IOException e) {
                log.error("Can't connect to ZooKeeper", e);
            }
        }
        throw new IOException("Unable to get host due to fail to connect to ZooKeeper server");
    }
}
