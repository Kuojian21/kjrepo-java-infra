package com.kjrepo.infra.buffer.trigger.legacy;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.common.trace.TraceIDUtils;

class XBufferTriggerBuilder<E, C> {

	private Supplier<C> containerFactory;
	private ToIntBiFunction<C, E> containerEnqueue;
	private LongSupplier containerCapacity;
	private XBufferTriggerRejectHandler<E> containerRejectHandler;

	private Lock enqueueLock;
	private IntSupplier enqueueTriggerConsumeThreshold;

	private Consumer<C> consumer;
	private LongSupplier consumeLinger;
	private BiConsumer<Throwable, C> consumeThrowableHandler;
	private ScheduledExecutorService consumeScheduledExecutor;
	private Executor consumeWorkerExecutor;

	public XBufferTriggerBuilder<E, C> setContainer(Supplier<C> containerFactory, BiConsumer<C, E> containerEnqueue) {
		this.containerFactory = containerFactory;
		this.containerEnqueue = (c, e) -> {
			containerEnqueue.accept(c, e);
			return 1;
		};
		return this;
	}

	public XBufferTriggerBuilder<E, C> setContainerEx(Supplier<C> containerFactory,
			ToIntBiFunction<C, E> containerEnqueue) {
		this.containerFactory = containerFactory;
		this.containerEnqueue = containerEnqueue;
		return this;
	}

	public XBufferTriggerBuilder<E, C> setContainerCapacity(long containerCapacity) {
		this.containerCapacity = () -> containerCapacity;
		return this;
	}

	public XBufferTriggerBuilder<E, C> setContainerRejectHandler(
			XBufferTriggerRejectHandler<E> containerRejectHandler) {
		this.containerRejectHandler = containerRejectHandler;
		return this;
	}

	public XBufferTriggerBuilder<E, C> enableEnqueueLock() {
		this.enqueueLock = new ReentrantLock();
		return this;
	}

	public XBufferTriggerBuilder<E, C> disableEnqueueLock() {
		this.enqueueLock = NOLOCK;
		return this;
	}

	public XBufferTriggerBuilder<E, C> setEnqueueTriggerConsumeThreshold(int enqueueTriggerConsumeThreshold) {
		this.enqueueTriggerConsumeThreshold = () -> enqueueTriggerConsumeThreshold;
		return this;
	}

	public XBufferTriggerBuilder<E, C> setConsumer(Consumer<C> consumer) {
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

	public XBufferTriggerBuilder<E, C> setConsumeLinger(long consumeLinger) {
		this.consumeLinger = () -> consumeLinger;
		return this;
	}

	public XBufferTriggerBuilder<E, C> setConsumeThrowableHandler(BiConsumer<Throwable, C> consumeThrowableHandler) {
		this.consumeThrowableHandler = consumeThrowableHandler;
		return this;
	}

	public XBufferTriggerBuilder<E, C> setConsumeScheduledExecutor(ScheduledExecutorService consumeScheduledExecutor) {
		this.consumeScheduledExecutor = consumeScheduledExecutor;
		return this;
	}

	public XBufferTriggerBuilder<E, C> setConsumeWorkerExecutor(Executor consumeWorkerExecutor) {
		this.consumeWorkerExecutor = consumeWorkerExecutor;
		return this;
	}

	public XBufferTrigger<E> build() {
		ensure();
		com.github.phantomthief.collection.BufferTrigger<E> trigger = new XBufferTriggerImpl<>(containerFactory, //
				containerEnqueue, //
				containerCapacity, //
				containerRejectHandler, //
				enqueueLock, //
				enqueueTriggerConsumeThreshold, //
				consumer, //
				consumeLinger, //
				consumeThrowableHandler, //
				consumeScheduledExecutor, //
				consumeWorkerExecutor);
		TermHelper.addTerm("buffer-trigger", () -> {
			trigger.manuallyDoTrigger();
		});
		return new XBufferTrigger<E>(trigger);
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
			containerRejectHandler = new XBufferTriggerRejectHandler<E>() {
				@Override
				public boolean onReject(E element, Condition condition) {
					XBufferTrigger.logger.info("Reject Element:{}", element);
					return true;
				}
			};
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
		if (consumeLinger == null) {
			consumeLinger = () -> 1000;
		}
		if (consumeThrowableHandler == null) {
			consumeThrowableHandler = (throwable, container) -> XBufferTrigger.logger
					.error("consume error, container:" + container, throwable);
		}
		if (consumeScheduledExecutor == null) {
			consumeScheduledExecutor = Executors.newSingleThreadScheduledExecutor(
					new ThreadFactoryBuilder().setNameFormat("buffer-trigger-%d").setDaemon(true).build());
		}
		if (consumeWorkerExecutor == null) {
			consumeWorkerExecutor = MoreExecutors.directExecutor();
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
