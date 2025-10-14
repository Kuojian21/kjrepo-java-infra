package com.kjrepo.infra.common.disruptor;

public class KDisruptorEvent<T> {

	private volatile T data;

	public T getData() {
		return data;
	}

	public void setData(T event) {
		this.data = event;
	}

}
