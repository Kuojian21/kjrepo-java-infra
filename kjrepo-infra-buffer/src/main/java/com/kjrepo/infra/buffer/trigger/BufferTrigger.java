package com.kjrepo.infra.buffer.trigger;

import org.slf4j.Logger;

import com.kjrepo.infra.buffer.trigger.builder.ContainerBufferTriggerBuilder;
import com.kjrepo.infra.buffer.trigger.builder.ContainerThresholdBatchBufferTriggerBuilder;
import com.kjrepo.infra.buffer.trigger.builder.ContainerThresholdBufferTriggerBuilder;
import com.kjrepo.infra.common.logger.LoggerUtils;

/**
 * com.github.phantomthief.collection.BufferTrigger<E>
 */
public interface BufferTrigger<E> extends AutoCloseable {

	public static <E> ContainerThresholdBatchBufferTriggerBuilder<E> batch() {
		return new ContainerThresholdBatchBufferTriggerBuilder<>();
	}

	public static <E, C> ContainerBufferTriggerBuilder<E, C> simple() {
		return new ContainerBufferTriggerBuilder<>();
	}

	public static <E, C> ContainerThresholdBufferTriggerBuilder<E, C> threshold() {
		return new ContainerThresholdBufferTriggerBuilder<>();
	}

	Logger logger = LoggerUtils.logger(BufferTrigger.class);

	void enqueue(E element);

	void manuallyDoTrigger();

	long getPendingChanges();

	@Override
	void close();
}
