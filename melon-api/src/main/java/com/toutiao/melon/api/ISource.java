package com.toutiao.melon.api;

import com.toutiao.melon.api.stream.Collector;

/**
 * 通用数据源
 */
public interface ISource extends IOutStream {

    void getEvents(Collector collector);
}
