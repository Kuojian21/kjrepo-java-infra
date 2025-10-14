package com.kjrepo.infra.common.disruptor;

import com.lmax.disruptor.EventFactory;

public class KDisruptorEventFactory<T> implements EventFactory<KDisruptorEvent<T>> {

	@Override
	public KDisruptorEvent<T> newInstance() {
		return new KDisruptorEvent<>();
	}

}
