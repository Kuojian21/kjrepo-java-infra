package com.kjrepo.infra.buffer.trigger.builder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;

import org.slf4j.Logger;

import com.kjrepo.infra.buffer.trigger.BufferTrigger;
import com.kjrepo.infra.buffer.trigger.impl.ThresholdBufferTriggerImpl;
import com.kjrepo.infra.buffer.trigger.impl.ThresholdBufferTriggerRejectHandler;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.term.TermHelper;

public class ThresholdBufferTriggerBuilder<E, C> extends BufferTriggerBuilder<E, C> {

	private final Logger logger = LoggerUtils.logger(getClass());

	private LongSupplier containerCapacity;
	private ThresholdBufferTriggerRejectHandler<E> containerRejectHandler;
	private IntSupplier consumerTriggerThreshold;

	public ThresholdBufferTriggerBuilder<E, C> setContainerCapacity(long containerCapacity) {
		this.containerCapacity = () -> containerCapacity;
		return this;
	}

	public ThresholdBufferTriggerBuilder<E, C> setContainerRejectHandler(
			ThresholdBufferTriggerRejectHandler<E> containerRejectHandler) {
		this.containerRejectHandler = containerRejectHandler;
		return this;
	}

	public ThresholdBufferTriggerBuilder<E, C> setConsumerTriggerThreshold(int consumerTriggerThreshold) {
		this.consumerTriggerThreshold = () -> consumerTriggerThreshold;
		return this;
	}

	@Override
	public void ensure() {
		super.ensure();
		if (containerCapacity == null) {
			containerCapacity = () -> Long.MAX_VALUE;
		}
		if (containerRejectHandler == null) {
			containerRejectHandler = new ThresholdBufferTriggerRejectHandler<E>() {
				@Override
				public boolean onReject(E element) {
					logger.info("Reject Element:{}", element);
					return true;
				}
			};
		}

		if (consumerTriggerThreshold == null) {
			consumerTriggerThreshold = () -> Integer.MAX_VALUE;
		}
	}

	@Override
	public BufferTrigger<E> build() {
		ensure();
		BufferTrigger<E> trigger = new ThresholdBufferTriggerImpl<>(containerFactory, //
				containerEnqueue, //
				containerCapacity, //
				containerRejectHandler, //
				enqueueLock, //
				consumerTriggerThreshold, //
				consumer, //
				consumerLinger, //
				consumerThrowableHandler, //
				consumerScheduledExecutor);
		TermHelper.addTerm("buffer-trigger", () -> {
			trigger.manuallyDoTrigger();
		});
		return trigger;
	}

	@Override
	public ThresholdBufferTriggerBuilder<E, C> setContainer(Supplier<C> containerFactory,
			BiConsumer<C, E> containerEnqueue) {
		super.setContainer(containerFactory, containerEnqueue);
		return this;
	}

	@Override
	public ThresholdBufferTriggerBuilder<E, C> setContainerEx(Supplier<C> containerFactory,
			ToIntBiFunction<C, E> containerEnqueue) {
		super.setContainerEx(containerFactory, containerEnqueue);
		return this;
	}

	@Override
	public ThresholdBufferTriggerBuilder<E, C> enableEnqueueLock() {
		super.enableEnqueueLock();
		return this;
	}

	@Override
	public ThresholdBufferTriggerBuilder<E, C> disableEnqueueLock() {
		super.disableEnqueueLock();
		return this;
	}

	@Override
	public ThresholdBufferTriggerBuilder<E, C> setConsumer(Consumer<C> consumer) {
		super.setConsumer(consumer);
		return this;
	}

	@Override
	public ThresholdBufferTriggerBuilder<E, C> setInterval(int interval, TimeUnit timeUnit) {
		super.setInterval(interval, timeUnit);
		return this;
	}

	@Override
	public ThresholdBufferTriggerBuilder<E, C> setConsumerLinger(long consumerLinger) {
		super.setConsumerLinger(consumerLinger);
		return this;
	}

	@Override
	public ThresholdBufferTriggerBuilder<E, C> setConsumerThrowableHandler(
			BiConsumer<Throwable, C> consumerThrowableHandler) {
		super.setConsumerThrowableHandler(consumerThrowableHandler);
		return this;
	}

	@Override
	public ThresholdBufferTriggerBuilder<E, C> setConsumerScheduledExecutor(
			ScheduledExecutorService consumerScheduledExecutor) {
		super.setConsumerScheduledExecutor(consumerScheduledExecutor);
		return this;
	}

}
