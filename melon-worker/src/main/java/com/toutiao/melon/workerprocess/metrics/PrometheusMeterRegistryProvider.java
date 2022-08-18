

package com.toutiao.melon.workerprocess.metrics;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import javax.inject.Provider;

public class PrometheusMeterRegistryProvider implements Provider<PrometheusMeterRegistry> {

    @Override
    public PrometheusMeterRegistry get() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }
}
