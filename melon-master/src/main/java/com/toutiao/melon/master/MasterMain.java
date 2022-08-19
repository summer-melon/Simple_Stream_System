package com.toutiao.melon.master;

import java.io.IOException;

public class MasterMain {

    public static void main(String[] args) throws InterruptedException, IOException {
        final MasterServer server = new MasterServer(6000);
        server.start();
        server.blockUntilShutdown();
    }
}
