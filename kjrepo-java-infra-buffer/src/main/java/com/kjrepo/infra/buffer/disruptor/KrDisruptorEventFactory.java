package com.kjrepo.infra.buffer.disruptor;

import com.lmax.disruptor.EventFactory;

public class KrDisruptorEventFactory<T> implements EventFactory<KrDisruptorEvent<T>> {

	@Override
	public KrDisruptorEvent<T> newInstance() {
		return new KrDisruptorEvent<>();
	}

}
