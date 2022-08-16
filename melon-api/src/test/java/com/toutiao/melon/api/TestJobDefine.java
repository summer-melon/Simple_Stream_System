package com.toutiao.melon.api;

import com.toutiao.melon.api.job.Job;
import com.toutiao.melon.api.stream.Collector;
import com.toutiao.melon.api.stream.Event;
import com.toutiao.melon.api.stream.OutGoingStream;
import org.junit.Test;

public class TestJobDefine {

    private static class KafkaSource implements ISource {
        @Override
        public void defineOutGoingStream(OutGoingStream outGoingStream) {

        }

        @Override
        public void getEvents(Collector collector) {

        }
    }

    private static class MySQLSink implements IOperator {
        @Override
        public void compute(Event event, Collector collector) {

        }

        @Override
        public void defineOutGoingStream(OutGoingStream outGoingStream) {

        }
    }

    @Test
    public void testJobDefine() {
        Job.newBuilder()
                .addSource("kafkaSource", KafkaSource.class, 1, 2)
                .addOperator("MySQLSink", MySQLSink.class, 1, 2)
                .addStream("KafkaSource", "MySQLSink", "transferStream")
                .build();
    }
}
