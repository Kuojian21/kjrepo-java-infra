package com.kjrepo.infra.buffer.trigger.builder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kjrepo.infra.buffer.trigger.BufferTrigger;
import com.kjrepo.infra.buffer.trigger.impl.ContainerBufferTriggerImpl;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.common.trace.TraceIDUtils;

public class ContainerBufferTriggerBuilder<E, C> {

	public static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("buffer-trigger-%d")
			.setDaemon(false).build();

	protected Supplier<C> containerFactory;
	protected ToIntBiFunction<C, E> containerEnqueue;
	protected Lock enqueueLock;
	protected Consumer<C> consumer;
	protected LongSupplier consumerLinger;
	protected BiConsumer<Throwable, C> consumerThrowableHandler;
	protected ScheduledExecutorService consumerScheduledExecutor;

	public ContainerBufferTriggerBuilder<E, C> setContainer(Supplier<C> containerFactory,
			BiConsumer<C, E> containerEnqueue) {
		this.containerFactory = containerFactory;
		return this.setContainerEx(containerFactory, (container, element) -> {
			containerEnqueue.accept(container, element);
			return 1;
		});
	}

	public ContainerBufferTriggerBuilder<E, C> setContainerEx(Supplier<C> containerFactory,
			ToIntBiFunction<C, E> containerEnqueue) {
		this.containerFactory = containerFactory;
		this.containerEnqueue = containerEnqueue;
		return this;
	}

	public ContainerBufferTriggerBuilder<E, C> enableEnqueueLock() {
		this.enqueueLock = new ReentrantLock();
		return this;
	}

	public ContainerBufferTriggerBuilder<E, C> disableEnqueueLock() {
		this.enqueueLock = NOLOCK;
		return this;
	}

	public ContainerBufferTriggerBuilder<E, C> setConsumer(Consumer<C> consumer) {
		this.consumer = c -> {
			String traceid = TraceIDUtils.get();
			try {
				TraceIDUtils.generate(traceid);
				consumer.accept(c);
			} finally {
				TraceIDUtils.set(traceid);
			}
		};
		return this;
	}

	public ContainerBufferTriggerBuilder<E, C> setInterval(int interval, TimeUnit timeUnit) {
		return this.setConsumerLinger(timeUnit.toMillis(interval));
	}

	public ContainerBufferTriggerBuilder<E, C> setConsumerLinger(long consumerLinger) {
		this.consumerLinger = () -> consumerLinger;
		return this;
	}

	public ContainerBufferTriggerBuilder<E, C> setConsumerThrowableHandler(
			BiConsumer<Throwable, C> consumerThrowableHandler) {
		this.consumerThrowableHandler = consumerThrowableHandler;
		return this;
	}

	public ContainerBufferTriggerBuilder<E, C> setConsumerScheduledExecutor(
			ScheduledExecutorService consumerScheduledExecutor) {
		this.consumerScheduledExecutor = consumerScheduledExecutor;
		return this;
	}

	public BufferTrigger<E> build() {
		ensure();
		BufferTrigger<E> trigger = new ContainerBufferTriggerImpl<>(containerFactory, //
				containerEnqueue, //
				enqueueLock, //
				consumer, //
				consumerLinger, //
				consumerThrowableHandler, //
				consumerScheduledExecutor);
		TermHelper.addTerm("buffer-trigger", () -> {
			trigger.manuallyDoTrigger();
		});
		return trigger;
	}

	public void ensure() {
		if (containerFactory == null) {
			throw new RuntimeException("does not set containerFactory!!");
		}
		if (containerEnqueue == null) {
			throw new RuntimeException("does not set containerEnqueue!!");
		}
		if (enqueueLock == null) {
			enqueueLock = new ReentrantLock();
		}
		if (consumer == null) {
			throw new RuntimeException("does not set consumer!!");
		}
		if (consumerLinger == null) {
			consumerLinger = () -> TimeUnit.MINUTES.toMillis(1);
		}
		if (consumerThrowableHandler == null) {
			consumerThrowableHandler = (throwable, container) -> BufferTrigger.logger
					.error("consume error, container:" + container, throwable);
		}
		if (consumerScheduledExecutor == null) {
			consumerScheduledExecutor = Executors.newSingleThreadScheduledExecutor(THREAD_FACTORY);
		}
	}

	private static final Lock NOLOCK = new Lock() {

		@Override
		public void lock() {
			// Do nothing
		}

		@Override
		public void unlock() {
			// Do nothing
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			throw new UnsupportedOperationException("Should not be called");
		}

		@Override
		public boolean tryLock() {
			throw new UnsupportedOperationException("Should not be called");
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			throw new UnsupportedOperationException("Should not be called");
		}

		@Override
		public Condition newCondition() {
			throw new UnsupportedOperationException("Should not be called");
		}
	};

}
