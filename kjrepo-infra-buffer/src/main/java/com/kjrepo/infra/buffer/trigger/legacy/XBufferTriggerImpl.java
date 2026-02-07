package com.kjrepo.infra.buffer.trigger.legacy;

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

import com.github.phantomthief.collection.BufferTrigger;
import com.github.phantomthief.util.MoreLocks;

class XBufferTriggerImpl<E, C> implements BufferTrigger<E> {

	private final Supplier<C> containerFactory;
	private final AtomicReference<ContainerHolder<C>> containerHolderRef;
	private final ToIntBiFunction<C, E> containerEnqueue;
	private final LongSupplier containerCapacity;
	private final XBufferTriggerRejectHandler<E> containerRejectHandler;
	private final Lock containerRLock;
	private final Lock containerWLock;
	private final Condition containerWCondition;

	private final Lock enqueueLock;
	private final IntSupplier enqueueTriggerConsumeThreshold;

	private final Consumer<C> consumer;
	private final LongSupplier consumeLinger;
	private final BiConsumer<Throwable, C> consumeThrowableHandler;
	private final ScheduledExecutorService consumeScheduledExecutor;
	private final Executor consumeWorkerExecutor;
	private final Lock consumeLock = new ReentrantLock();
	private final AtomicBoolean comsumeRunning = new AtomicBoolean();

	public XBufferTriggerImpl(Supplier<C> containerFactory, //
			ToIntBiFunction<C, E> containerEnqueue, //
			LongSupplier containerCapacity, //
			XBufferTriggerRejectHandler<E> containerRejectHandler, //
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
		this.consumeLinger = comsumeLinger;
		this.consumeThrowableHandler = comsumeThrowableHandler;
		this.consumeScheduledExecutor = consumeScheduledExecutor;
		this.consumeWorkerExecutor = consumeWorkerExecutor;
		this.consumeScheduledExecutor.schedule(new ConsumerRunnable(), this.consumeLinger.getAsLong(),
				TimeUnit.MILLISECONDS);
	}

	@Override
	public void enqueue(E element) {
		if (containerHolderRef.get().counter.get() >= containerCapacity.getAsLong()) {
			if (MoreLocks.supplyWithLock(containerWLock, () -> {
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
		MoreLocks.runWithLock(containerRLock, () -> {
			ContainerHolder<C> containerHolder = containerHolderRef.get();
			containerHolder.counter.addAndGet(MoreLocks.supplyWithLock(enqueueLock,
					() -> containerEnqueue.applyAsInt(containerHolder.container, element)));
		});
		if (containerHolderRef.get().counter.get() >= enqueueTriggerConsumeThreshold.getAsInt()) {
			MoreLocks.runWithTryLock(consumeLock, () -> {
				if (containerHolderRef.get().counter.get() >= enqueueTriggerConsumeThreshold.getAsInt()
						&& !comsumeRunning.get()) {
					comsumeRunning.set(true);
					consumeScheduledExecutor.execute(() -> doConsume());
				}
			});
		}
	}

	private void doConsume() {
		MoreLocks.runWithLock(consumeLock, () -> {
			try {
				comsumeRunning.set(true);
				C data = MoreLocks.supplyWithLock(containerWLock, () -> {
					C container = containerHolderRef.getAndSet(new ContainerHolder<>(containerFactory.get())).container;
					containerWCondition.signalAll();
					return container;
				});
				this.consumeWorkerExecutor.execute(() -> {
					try {
						consumer.accept(data);
					} catch (Throwable throwable) {
						consumeThrowableHandler.accept(throwable, data);
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
				consumeScheduledExecutor.schedule(this, consumeLinger.getAsLong(), TimeUnit.MILLISECONDS);
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

	@Override
	public void manuallyDoTrigger() {
		this.doConsume();
	}

	@Override
	public long getPendingChanges() {
		return this.containerHolderRef.get().counter.get();
	}

	@Override
	public void close() {
		this.doConsume();
	}

}
