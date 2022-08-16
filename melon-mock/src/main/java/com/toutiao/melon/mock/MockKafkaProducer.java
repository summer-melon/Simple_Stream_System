package com.toutiao.melon.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toutiao.melon.mock.config.Config;
import com.toutiao.melon.mock.entity.Event;
import com.toutiao.melon.mock.faker.StatelessBigFaker;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(MockKafkaProducer.class);

    public static void main(String[] args) throws InterruptedException {
        StatelessBigFaker faker = new StatelessBigFaker(10, 10);
        ObjectMapper objectMapper = new ObjectMapper();
        KafkaProducer<String, String> producer = new KafkaProducer<>(Config.KAFKA_PROPS);

        while (true) {
            TimeUnit.MILLISECONDS.sleep(Config.SEND_DELAY);
            Event event = faker.mock();
            log.info("Faker mock event: {}", event);
            String value = null;
            try {
                value = objectMapper.writeValueAsString(event);
            } catch (JsonProcessingException e) {
                log.error("Json Parse {} Error, {}", event, e.getMessage());
            }
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(Config.TOPIC, event.getWord().toLowerCase(), value);
            producer.send(record);
        }
    }
}
