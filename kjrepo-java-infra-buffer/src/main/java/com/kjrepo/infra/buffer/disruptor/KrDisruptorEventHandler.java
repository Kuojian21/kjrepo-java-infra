package com.kjrepo.infra.buffer.disruptor;

import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;
import com.lmax.disruptor.EventHandler;

public class KrDisruptorEventHandler<T> implements EventHandler<KrDisruptorEvent<T>> {

	private final Logger logger = LoggerUtils.logger(getClass());
	private final KrDisruptorHandler<T> handler;

	public KrDisruptorEventHandler(KrDisruptorHandler<T> handler) {
		super();
		this.handler = handler;
	}

	@Override
	public void onEvent(KrDisruptorEvent<T> event, long sequence, boolean endOfBatch) throws Exception {
		logger.debug("data:{} sequence:{} endOfBatch:{}", event.getData(), sequence, endOfBatch);
		this.handler.onEvent(event.getData());
	}

}
