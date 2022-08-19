package com.toutiao.melon.worker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.toutiao.melon.shared.GuiceModule;

public class WorkerMain {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new GuiceModule());
        final WorkerServer server = injector.getInstance(WorkerServer.class);
        server.startAndBlock();
    }
}
