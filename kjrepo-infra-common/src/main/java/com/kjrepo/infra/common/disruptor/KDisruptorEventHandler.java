package com.kjrepo.infra.common.disruptor;

import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;
import com.lmax.disruptor.EventHandler;

public class KDisruptorEventHandler<T> implements EventHandler<KDisruptorEvent<T>> {

	private final Logger logger = LoggerUtils.logger();
	private final KDisruptorHandler<T> handler;

	public KDisruptorEventHandler(KDisruptorHandler<T> handler) {
		super();
		this.handler = handler;
	}

	@Override
	public void onEvent(KDisruptorEvent<T> event, long sequence, boolean endOfBatch) throws Exception {
		logger.debug("data:{} sequence:{} endOfBatch:{}", event.getData(), sequence, endOfBatch);
		this.handler.onEvent(event.getData());
	}

}
