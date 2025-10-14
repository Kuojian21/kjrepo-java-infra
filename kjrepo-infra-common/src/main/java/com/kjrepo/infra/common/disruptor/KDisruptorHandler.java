package com.kjrepo.infra.common.disruptor;

public interface KDisruptorHandler<T> {

	void onEvent(T data);

}
