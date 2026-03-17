package com.kjrepo.infra.buffer.trigger.impl;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;

import com.kjrepo.infra.common.utils.LockUtils;

public class ContainerThresholdBufferTriggerImpl<E, C> extends ContainerBufferTriggerImpl<E, C> {

	private final LongSupplier containerCapacity;
	private final ContainerThresholdBufferTriggerRejectHandler<E> containerRejectHandler;
	private final IntSupplier consumerTriggerThreshold;

	public ContainerThresholdBufferTriggerImpl(Supplier<C> containerFactory, //
			ToIntBiFunction<C, E> containerEnqueue, //
			LongSupplier containerCapacity, //
			ContainerThresholdBufferTriggerRejectHandler<E> containerRejectHandler, //
			Lock enqueueLock, //
			IntSupplier consumerTriggerThreshold, //
			Consumer<C> consumer, //
			LongSupplier comsumeLinger, //
			BiConsumer<Throwable, C> comsumeThrowableHandler, //
			ScheduledExecutorService consumeScheduledExecutor) {
		super(containerFactory, containerEnqueue, enqueueLock, consumer, comsumeLinger, comsumeThrowableHandler,
				consumeScheduledExecutor);
		this.containerCapacity = containerCapacity;
		this.containerRejectHandler = containerRejectHandler;
		this.consumerTriggerThreshold = consumerTriggerThreshold;
	}

	@Override
	public void enqueue(E element) {
		if (containerRef.get().counter.get() >= containerCapacity.getAsLong()) {
			containerRejectHandler.onReject(element);
			return;
		}
		super.enqueue(element);
		if (containerRef.get().counter.get() >= consumerTriggerThreshold.getAsInt() && !consumerRunning.get()) {
			LockUtils.runInLock(containerWLock, () -> {
				if (containerRef.get().counter.get() >= consumerTriggerThreshold.getAsInt() && !consumerRunning.get()) {
					consumerRunning.set(true);
					consumerScheduledExecutor.execute(() -> consume());
				}
			});
		}
	}

}
