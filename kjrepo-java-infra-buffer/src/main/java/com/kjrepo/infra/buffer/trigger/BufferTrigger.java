package com.kjrepo.infra.buffer.trigger;

import org.slf4j.Logger;

import com.kjrepo.infra.buffer.trigger.builder.BufferTriggerBuilder;
import com.kjrepo.infra.buffer.trigger.builder.ThresholdBatchBufferTriggerBuilder;
import com.kjrepo.infra.buffer.trigger.builder.ThresholdBufferTriggerBuilder;
import com.kjrepo.infra.common.logger.LoggerUtils;

/**
 * com.github.phantomthief.collection.BufferTrigger<E>
 */
public interface BufferTrigger<E> extends AutoCloseable {

	public static <E> ThresholdBatchBufferTriggerBuilder<E> batch() {
		return new ThresholdBatchBufferTriggerBuilder<>();
	}

	public static <E, C> BufferTriggerBuilder<E, C> simple() {
		return new BufferTriggerBuilder<>();
	}

	public static <E, C> ThresholdBufferTriggerBuilder<E, C> threshold() {
		return new ThresholdBufferTriggerBuilder<>();
	}

	Logger logger = LoggerUtils.logger(BufferTrigger.class);

	void enqueue(E element);

	void manuallyDoTrigger();

	long getPendingChanges();

	@Override
	void close();
}
