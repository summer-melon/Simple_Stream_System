package com.toutiao.melon.example;

import com.toutiao.melon.api.IOperator;
import com.toutiao.melon.api.stream.Collector;
import com.toutiao.melon.api.stream.Event;
import com.toutiao.melon.api.stream.OutGoingStream;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class TwoReduceOperator implements IOperator {

    Map<String, Integer> wordCountMap = new ConcurrentHashMap<>();
    private Timer timer;

    public TwoReduceOperator() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!wordCountMap.isEmpty()) {
                    long now = System.currentTimeMillis();
                    for (Map.Entry<String, Integer> wordCount : wordCountMap.entrySet()) {
                        String wordKey = wordCount.getKey();
                        int wordValue = wordCount.getValue();
                        System.out.println("two: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(new Date(now))
                                + " " + wordKey + "," + wordValue);
                    }
                    wordCountMap.clear();
                }
            }
        }, 0, 20 * 1000);
    }

    @Override
    public void compute(Event event, Collector collector) {
        String word = event.getStringByName("twoWord");
        int count = event.getIntByName("twoCount");
        wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + count);
    }

    @Override
    public void defineOutGoingStream(OutGoingStream outGoingStream) {

    }
}
