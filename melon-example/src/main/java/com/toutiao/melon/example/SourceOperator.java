package com.toutiao.melon.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.toutiao.melon.api.ISource;
import com.toutiao.melon.api.stream.Collector;
import com.toutiao.melon.api.stream.Field;
import com.toutiao.melon.api.stream.FieldType;
import com.toutiao.melon.api.stream.OutGoingStream;
import com.toutiao.melon.api.stream.Value;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class SourceOperator implements ISource {

    private static Properties properties = new Properties();
    private KafkaConsumer<String, String> kafkaConsumer;
    private List<String> topics = new ArrayList<>();
    private ObjectMapper objectMapper;

    static {
        properties.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop102:9092,hadoop103:9092");
        properties.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(
                ConsumerConfig.GROUP_ID_CONFIG, "test");
    }

    public SourceOperator() {
        kafkaConsumer = new KafkaConsumer<>(properties);
        topics.add("bytedance-youth-training-camp");
        kafkaConsumer.subscribe(topics);
        objectMapper = new ObjectMapper();
    }

    @Override
    public void defineOutGoingStream(OutGoingStream outGoingStream) {
        outGoingStream.addSchema(
                "sourceStream",
                new Field("sourceWord", FieldType.STRING),
                new Field("sourceCount", FieldType.INT)
        );
    }

    @Override
    public void getEvents(Collector collector) {
        while (true) {
            ConsumerRecords<String, String> consumerRecords =
                    kafkaConsumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                try {
                    ObjectNode node = (ObjectNode) objectMapper.readTree(consumerRecord.value());
                    String word = node.get("word").toString();
                    int count = node.get("count").intValue();
                    collector.emit("sourceStream",
                            new Value("sourceWord", word), new Value("sourceCount", count));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
