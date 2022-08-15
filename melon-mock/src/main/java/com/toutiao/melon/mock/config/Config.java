package com.toutiao.melon.mock.config;

import java.util.Properties;
import org.apache.kafka.clients.producer.ProducerConfig;

public class Config {
    public static final String BOOTSTRAP_SERVERS = "hadoop101:9092,hadoop102:9092";
    public static final String PROTOCOL = "PLAINTEXT";
    public static final String TOPIC = "bytedance-youth-training-camp";
    public static final Properties KAFKA_PROPS = new Properties();
    public static final int SEND_DELAY = 1000;

    static {
        KAFKA_PROPS.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Config.BOOTSTRAP_SERVERS);
        KAFKA_PROPS.put(ProducerConfig.SECURITY_PROVIDERS_CONFIG, Config.PROTOCOL);
        KAFKA_PROPS.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        KAFKA_PROPS.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        // 请求的最长等待时间
        KAFKA_PROPS.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30 * 1000);
        // 设置客户端内部重试次数
        KAFKA_PROPS.put(ProducerConfig.RETRIES_CONFIG, 5);
        // 设置客户端内部重试间隔
        KAFKA_PROPS.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 3000);
    }
}
