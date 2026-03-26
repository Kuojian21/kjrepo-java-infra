package com.kjrepo.infra.buffer.disruptor;

public interface KrDisruptorHandler<T> {

	void onEvent(T data);

}
