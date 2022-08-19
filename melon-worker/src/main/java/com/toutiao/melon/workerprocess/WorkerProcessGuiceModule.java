package com.toutiao.melon.workerprocess;

import com.toutiao.melon.shared.GuiceModule;
import com.toutiao.melon.workerprocess.metrics.PrometheusMeterRegistryProvider;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import javax.inject.Singleton;

public class WorkerProcessGuiceModule extends GuiceModule {

    @Override
    protected void configure() {
        super.configure();
        bind(PrometheusMeterRegistry.class)
                .toProvider(PrometheusMeterRegistryProvider.class)
                .in(Singleton.class);
    }
}
