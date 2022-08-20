package com.toutiao.melon.example;

import com.toutiao.melon.api.IOperator;
import com.toutiao.melon.api.stream.Collector;
import com.toutiao.melon.api.stream.Event;
import com.toutiao.melon.api.stream.Field;
import com.toutiao.melon.api.stream.FieldType;
import com.toutiao.melon.api.stream.OutGoingStream;
import com.toutiao.melon.api.stream.Value;

public class KeyByOperator implements IOperator {

    @Override
    public void compute(Event event, Collector collector) {
        String word = event.getStringByName("mapWord");
        int count = event.getIntByName("mapCount");
        int hashCode = word.hashCode();
        if (hashCode % 2 == 0) {
            collector.emit(
                    "oneReduceStream",
                    new Value("oneWord", word),
                    new Value("oneCount", count)
            );
        } else {
            collector.emit(
                    "twoReduceStream",
                    new Value("twoWord", word),
                    new Value("twoCount", count)
            );
        }
    }

    @Override
    public void defineOutGoingStream(OutGoingStream outGoingStream) {
        outGoingStream.addSchema(
                        "oneReduceStream",
                        new Field("oneWord", FieldType.STRING),
                        new Field("oneCount", FieldType.INT))
                      .addSchema(
                       "twoReduceStream",
                        new Field("twoWord", FieldType.STRING),
                        new Field("twoCount", FieldType.INT));
    }
}
