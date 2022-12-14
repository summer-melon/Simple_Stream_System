package com.toutiao.melon.workerprocess;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class WorkerProcessMain {

    public static void main(String[] args) throws Throwable {
        Injector injector = Guice.createInjector(new WorkerProcessGuiceModule());
        WorkerProcessServer server = injector.getInstance(WorkerProcessServer.class);
        server.start(args[0], args[1]);
        server.blockUntilShutdown();
    }
}
