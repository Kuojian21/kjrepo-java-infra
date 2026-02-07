package com.kjrepo.infra.buffer.trigger.legacy;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.Function;

import com.github.phantomthief.collection.BufferTrigger;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kjrepo.infra.buffer.trigger.BufferTriggerBuilder;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.common.trace.TraceIDUtils;

class ContainerBufferTriggerBuilder<E, C> {

	private Supplier<C> containerFactory;
	private BiConsumer<C, E> containerEnqueue;
	private Function<C, Integer> containerLength;

	private Lock enqueueLock;

	private Consumer<C> consumer;
	private LongSupplier consumeLinger;
	private BiConsumer<Throwable, C> consumeThrowableHandler;
	private ScheduledExecutorService consumeScheduledExecutor;

	public ContainerBufferTriggerBuilder<E, C> setContainer(Supplier<C> containerFactory,
			BiConsumer<C, E> containerEnqueue) {
		setContainer(containerFactory, containerEnqueue, null);
		return this;
	}

	public ContainerBufferTriggerBuilder<E, C> setContainer(Supplier<C> containerFactory,
			BiConsumer<C, E> containerEnqueue, Function<C, Integer> containerLength) {
		this.containerFactory = containerFactory;
		this.containerEnqueue = containerEnqueue;
		this.containerLength = containerLength;
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

	public ContainerBufferTriggerBuilder<E, C> setConsumeLinger(long consumeLinger) {
		this.consumeLinger = () -> consumeLinger;
		return this;
	}

	public ContainerBufferTriggerBuilder<E, C> setConsumeThrowableHandler(
			BiConsumer<Throwable, C> consumeThrowableHandler) {
		this.consumeThrowableHandler = consumeThrowableHandler;
		return this;
	}

	public ContainerBufferTriggerBuilder<E, C> setConsumeScheduledExecutor(
			ScheduledExecutorService consumeScheduledExecutor) {
		this.consumeScheduledExecutor = consumeScheduledExecutor;
		return this;
	}

	public BufferTrigger<E> build() {
		ensure();
		BufferTrigger<E> trigger = new ContainerBufferTriggerImpl<>(containerFactory, //
				containerEnqueue, //
				containerLength, //
				enqueueLock, //
				consumer, //
				consumeLinger, //
				consumeThrowableHandler, //
				consumeScheduledExecutor);
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
		if (containerLength == null) {
			containerLength = c -> {
				if (c instanceof Map) {
					return ((Map<?, ?>) c).size();
				} else if (c instanceof Collection) {
					return ((Collection<?>) c).size();
				} else {
					return -1;
				}
			};
		}
		if (enqueueLock == null) {
			enqueueLock = new ReentrantLock();
		}
		if (consumer == null) {
			throw new RuntimeException("does not set consumer!!");
		}
		if (consumeLinger == null) {
			consumeLinger = () -> TimeUnit.SECONDS.toMillis(1);
		}
		if (consumeThrowableHandler == null) {
			consumeThrowableHandler = (throwable, container) -> BufferTriggerBuilder.logger
					.error("consume error, container:" + container, throwable);
		}
		if (consumeScheduledExecutor == null) {
			consumeScheduledExecutor = Executors.newSingleThreadScheduledExecutor(
					new ThreadFactoryBuilder().setNameFormat("buffer-trigger-%d").setDaemon(true).build());
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
