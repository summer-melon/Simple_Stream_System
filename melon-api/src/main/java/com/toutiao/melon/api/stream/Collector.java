package com.toutiao.melon.api.stream;

/**
 * 将Event发送给下游目标流
 */
public interface Collector {

    void emit(String targetStreamId, Value... events);
}
