package com.toutiao.melon.example;

import com.toutiao.melon.api.IJob;
import com.toutiao.melon.api.job.Job;

public class WordCountJob implements IJob {

    @Override
    public Job getJob() throws Exception {
        return Job.newBuilder()
                .addSource("kafkaSource", SourceOperator.class, 2, 1)
                .addOperator("map", MapOperator.class, 2, 2)
                .addOperator("keyBy", KeyByOperator.class, 2, 2)
                .addOperator("oneReduce", OneReduceOperator.class, 1, 1)
                .addOperator("twoReduce", TwoReduceOperator.class, 1, 1)
                .addStream("kafkaSource", "map", "sourceStream")
                .addStream("map", "keyBy", "mapStream")
                .addStream("keyBy", "oneReduce", "oneReduceStream")
                .addStream("keyBy", "twoReduce", "twoReduceStream")
                .build();

    }
}
