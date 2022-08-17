package com.toutiao.melon.example;

import com.toutiao.melon.api.IOperator;
import com.toutiao.melon.api.stream.Collector;
import com.toutiao.melon.api.stream.Event;
import com.toutiao.melon.api.stream.Field;
import com.toutiao.melon.api.stream.FieldType;
import com.toutiao.melon.api.stream.OutGoingStream;
import com.toutiao.melon.api.stream.Value;

public class MapOperator implements IOperator {

    @Override
    public void compute(Event event, Collector collector) {
        String word = event.getStringByName("sourceWord");
        int count = event.getIntByName("sourceCount");
        collector.emit("mapStream",
                new Value("mapWord", word.toLowerCase()), new Value("mapCount", count));
    }

    @Override
    public void defineOutGoingStream(OutGoingStream outGoingStream) {
        outGoingStream.addSchema("mapStream",
                new Field("mapWord", FieldType.STRING), new Field("mapCount", FieldType.INT));
    }
}
