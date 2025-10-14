package com.kjrepo.infra.common.buffer;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;

class BufferTriggerImpl<E, C> implements BufferTrigger<E, C> {

	private final Supplier<C> containerFactory;
	private final AtomicReference<ContainerHolder<C>> containerHolderRef;
	private final ToIntBiFunction<C, E> containerEnqueue;
	private final LongSupplier containerCapacity;
	private final BufferTriggerRejectHandler<E> containerRejectHandler;
	private final Lock containerRLock;
	private final Lock containerWLock;
	private final Condition containerWCondition;

	private final Lock enqueueLock;
	private final IntSupplier enqueueTriggerConsumeThreshold;

	private final Consumer<C> consumer;
	private final LongSupplier comsumeLinger;
	private final BiConsumer<Throwable, C> comsumeThrowableHandler;
	private final ScheduledExecutorService consumeScheduledExecutor;
	private final Executor consumeWorkerExecutor;
	private final Lock consumeLock = new ReentrantLock();
	private final AtomicBoolean comsumeRunning = new AtomicBoolean();

	public BufferTriggerImpl(Supplier<C> containerFactory, //
			ToIntBiFunction<C, E> containerEnqueue, //
			LongSupplier containerCapacity, //
			BufferTriggerRejectHandler<E> containerRejectHandler, //
			Lock enqueueLock, //
			IntSupplier enqueueTriggerConsumeThreshold, //
			Consumer<C> consumer, //
			LongSupplier comsumeLinger, //
			BiConsumer<Throwable, C> comsumeThrowableHandler, //
			ScheduledExecutorService consumeScheduledExecutor, //
			Executor consumeWorkerExecutor) {
		super();
		this.containerFactory = containerFactory;
		this.containerHolderRef = new AtomicReference<>(new ContainerHolder<>(this.containerFactory.get()));
		this.containerEnqueue = containerEnqueue;
		this.containerCapacity = containerCapacity;
		this.containerRejectHandler = containerRejectHandler;
		ReentrantReadWriteLock containerLock = new ReentrantReadWriteLock();
		this.containerRLock = containerLock.readLock();
		this.containerWLock = containerLock.writeLock();
		this.containerWCondition = this.containerWLock.newCondition();
		this.enqueueLock = enqueueLock;
		this.enqueueTriggerConsumeThreshold = enqueueTriggerConsumeThreshold;
		this.consumer = consumer;
		this.comsumeLinger = comsumeLinger;
		this.comsumeThrowableHandler = comsumeThrowableHandler;
		this.consumeScheduledExecutor = consumeScheduledExecutor;
		this.consumeWorkerExecutor = consumeWorkerExecutor;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				doConsume();
			}
		});
	}

	@Override
	public void enqueue(E element) {
		if (containerHolderRef.get().counter.get() >= containerCapacity.getAsLong()) {
			if (BufferTriggerUtils.runInLock(containerWLock, () -> {
				ContainerHolder<C> containerHolder = containerHolderRef.get();
				if (containerHolder.counter.get() >= containerCapacity.getAsLong()) {
					return containerRejectHandler.onReject(element, containerWCondition);
				} else {
					return false;
				}
			})) {
				return;
			}
		}
		BufferTriggerUtils.runInLock(containerRLock, () -> {
			ContainerHolder<C> containerHolder = containerHolderRef.get();
			containerHolder.counter.addAndGet(BufferTriggerUtils.runInLock(enqueueLock,
					() -> containerEnqueue.applyAsInt(containerHolder.container, element)));
		});
		if (containerHolderRef.get().counter.get() >= enqueueTriggerConsumeThreshold.getAsInt()) {
			BufferTriggerUtils.runInTryLock(consumeLock, () -> {
				if (containerHolderRef.get().counter.get() >= enqueueTriggerConsumeThreshold.getAsInt()
						&& !comsumeRunning.get()) {
					comsumeRunning.set(true);
					consumeScheduledExecutor.execute(() -> doConsume());
				}
			});
		}
	}

	private void doConsume() {
		BufferTriggerUtils.runInLock(consumeLock, () -> {
			try {
				comsumeRunning.set(true);
				C data = BufferTriggerUtils.runInLock(containerWLock, () -> {
					C container = containerHolderRef.getAndSet(new ContainerHolder<>(containerFactory.get())).container;
					containerWCondition.signalAll();
					return container;
				});
				this.consumeWorkerExecutor.execute(() -> {
					try {
						consumer.accept(data);
					} catch (Throwable throwable) {
						comsumeThrowableHandler.accept(throwable, data);
					}
				});
			} finally {
				comsumeRunning.set(false);
			}
		});
	}

	class ConsumerRunnable implements Runnable {
		@Override
		public void run() {
			try {
				doConsume();
			} finally {
				consumeScheduledExecutor.schedule(this, comsumeLinger.getAsLong(), TimeUnit.MILLISECONDS);
			}
		}
	}

	static class ContainerHolder<C> {
		final C container;
		final AtomicLong counter = new AtomicLong(0L);

		public ContainerHolder(C container) {
			super();
			this.container = container;
		}
	}

}
