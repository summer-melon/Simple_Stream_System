package com.toutiao.melon.shared;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.toutiao.melon.shared.provider.AppPropertiesProvider;
import com.toutiao.melon.shared.provider.ZooKeeperConnectionProvider;
import com.toutiao.melon.shared.wrapper.ZooKeeperConnection;

import java.util.Properties;

/**
 * 定义装配规则
 */
public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        // 饿汉式
        bind(Properties.class).toProvider(AppPropertiesProvider.class).asEagerSingleton();

        bind(ZooKeeperConnection.class).toProvider(ZooKeeperConnectionProvider.class).in(Singleton.class);
    }
}
