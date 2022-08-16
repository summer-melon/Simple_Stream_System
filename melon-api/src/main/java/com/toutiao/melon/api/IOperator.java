package com.toutiao.melon.api;

import com.toutiao.melon.api.stream.Collector;
import com.toutiao.melon.api.stream.Event;

/**
 * 计算节点
 */
public interface IOperator extends IOutStream {

    void compute(Event event, Collector collector);
}
