package com.kjrepo.infra.buffer.disruptor;

public class KrDisruptorEvent<T> {

	private volatile T data;

	public T getData() {
		return data;
	}

	public void setData(T event) {
		this.data = event;
	}

}
