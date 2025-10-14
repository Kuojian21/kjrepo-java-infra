package com.kjrepo.infra.common.buffer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;

import org.slf4j.Logger;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class BufferTriggerBuilder<E, C> {

	private static final Logger logger = LoggerUtils.logger();

	private Supplier<C> containerFactory;
	private ToIntBiFunction<C, E> containerEnqueue;
	private LongSupplier containerCapacity;
	private BufferTriggerRejectHandler<E> containerRejectHandler;

	private Lock enqueueLock;
	private IntSupplier enqueueTriggerConsumeThreshold;

	private Consumer<C> consumer;
	private LongSupplier comsumeLinger;
	private BiConsumer<Throwable, C> comsumeThrowableHandler;
	private ScheduledExecutorService consumeScheduledExecutor;
	private Executor consumeWorkerExecutor;

	public BufferTriggerBuilder<E, C> setContainer(Supplier<C> containerFactory, BiConsumer<C, E> containerEnqueue) {
		this.containerFactory = containerFactory;
		this.containerEnqueue = (c, e) -> {
			containerEnqueue.accept(c, e);
			return 1;
		};
		return this;
	}

	public BufferTriggerBuilder<E, C> setContainerEx(Supplier<C> containerFactory, ToIntBiFunction<C, E> containerEnqueue) {
		this.containerFactory = containerFactory;
		this.containerEnqueue = containerEnqueue;
		return this;
	}

	public BufferTriggerBuilder<E, C> setContainerCapacity(long containerCapacity) {
		this.containerCapacity = () -> containerCapacity;
		return this;
	}

	public BufferTriggerBuilder<E, C> setContainerRejectHandler(BufferTriggerRejectHandler<E> containerRejectHandler) {
		this.containerRejectHandler = containerRejectHandler;
		return this;
	}

	public BufferTriggerBuilder<E, C> enableEnqueueLock() {
		this.enqueueLock = new ReentrantLock();
		return this;
	}

	public BufferTriggerBuilder<E, C> disableEnqueueLock() {
		this.enqueueLock = BufferTriggerUtils.NOLOCK;
		return this;
	}

	public BufferTriggerBuilder<E, C> setEnqueueTriggerConsumeThreshold(int enqueueTriggerConsumeThreshold) {
		this.enqueueTriggerConsumeThreshold = () -> enqueueTriggerConsumeThreshold;
		return this;
	}

	public BufferTriggerBuilder<E, C> setConsumer(Consumer<C> consumer) {
		this.consumer = consumer;
		return this;
	}

	public BufferTriggerBuilder<E, C> setComsumeLinger(long comsumeLinger) {
		this.comsumeLinger = () -> comsumeLinger;
		return this;
	}

	public BufferTriggerBuilder<E, C> setComsumeThrowableHandler(BiConsumer<Throwable, C> comsumeThrowableHandler) {
		this.comsumeThrowableHandler = comsumeThrowableHandler;
		return this;
	}

	public BufferTriggerBuilder<E, C> setConsumeScheduledExecutor(ScheduledExecutorService consumeScheduledExecutor) {
		this.consumeScheduledExecutor = consumeScheduledExecutor;
		return this;
	}

	public BufferTriggerBuilder<E, C> setConsumeWorkerExecutor(Executor consumeWorkerExecutor) {
		this.consumeWorkerExecutor = consumeWorkerExecutor;
		return this;
	}

	public BufferTriggerImpl<E, C> build() {
		ensure();
		return new BufferTriggerImpl<>(containerFactory, //
				containerEnqueue, //
				containerCapacity, //
				containerRejectHandler, //
				enqueueLock, //
				enqueueTriggerConsumeThreshold, //
				consumer, //
				comsumeLinger, //
				comsumeThrowableHandler, //
				consumeScheduledExecutor, //
				consumeWorkerExecutor);
	}

	public void ensure() {
		if (containerFactory == null) {
			throw new RuntimeException("does not set containerFactory!!");
		}
		if (containerEnqueue == null) {
			throw new RuntimeException("does not set containerEnqueue!!");
		}
		if (containerCapacity == null) {
			containerCapacity = () -> Long.MAX_VALUE;
		}
		if (containerRejectHandler == null) {
			containerRejectHandler = BufferTriggerUtils.defaultRejectHandler();
		}
		if (enqueueLock == null) {
			enqueueLock = new ReentrantLock();
		}
		if (enqueueTriggerConsumeThreshold == null) {
			enqueueTriggerConsumeThreshold = () -> Integer.MAX_VALUE;
		}
		if (consumer == null) {
			throw new RuntimeException("does not set consumer!!");
		}
		if (comsumeLinger == null) {
			comsumeLinger = () -> 1000;
		}
		if (comsumeThrowableHandler == null) {
			comsumeThrowableHandler = (throwable, container) -> logger.error("consume error, container:" + container,
					throwable);
		}
		if (consumeScheduledExecutor == null) {
			consumeScheduledExecutor = Executors.newSingleThreadScheduledExecutor(
					new ThreadFactoryBuilder().setNameFormat("Ktrigger-%d").setDaemon(true).build());
		}
		if (consumeWorkerExecutor == null) {
			consumeWorkerExecutor = MoreExecutors.directExecutor();
		}
	}
}
