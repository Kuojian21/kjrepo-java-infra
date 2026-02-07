package com.kjrepo.infra.buffer.trigger.legacy;

import org.slf4j.Logger;

import com.kjrepo.infra.buffer.trigger.builder.GenericBatchConsumerTriggerBuilder;
import com.kjrepo.infra.buffer.trigger.builder.GenericSimpleBufferTriggerBuilder;
import com.kjrepo.infra.common.logger.LoggerUtils;

class XBufferTrigger<E> implements com.github.phantomthief.collection.BufferTrigger<E> {

	public static final Logger logger = LoggerUtils.logger(XBufferTrigger.class);

	public static <E, C> XBufferTriggerBuilder<E, C> builder() {
		return new XBufferTriggerBuilder<>();
	}

	public static <E, C> GenericSimpleBufferTriggerBuilder<E, C> simple() {
		return new GenericSimpleBufferTriggerBuilder<>();
	}

	public static <E> GenericBatchConsumerTriggerBuilder<E> batchBlocking() {
		return new GenericBatchConsumerTriggerBuilder<>();
	}

	private final com.github.phantomthief.collection.BufferTrigger<E> trigger;

	public XBufferTrigger(com.github.phantomthief.collection.BufferTrigger<E> trigger) {
		super();
		this.trigger = trigger;
	}

	@Override
	public void enqueue(E element) {
		this.trigger.enqueue(element);
	}

	@Override
	public void manuallyDoTrigger() {
		this.trigger.manuallyDoTrigger();
	}

	@Override
	public long getPendingChanges() {
		return this.trigger.getPendingChanges();
	}

	@Override
	public void close() {
		this.trigger.close();
	}

}
