package com.kjrepo.infra.buffer.trigger.impl;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;

import com.kjrepo.infra.buffer.trigger.BufferTrigger;
import com.kjrepo.infra.common.utils.LockUtils;

public class BufferTriggerImpl<E, C> implements BufferTrigger<E> {

	protected final Supplier<C> containerFactory;
	protected final AtomicReference<ContainerHolder> containerRef;
	protected final ToIntBiFunction<C, E> containerEnqueue;
	protected final Lock containerRLock;
	protected final Lock containerWLock;
	protected final Lock enqueueLock;
	protected final Consumer<C> consumer;
	protected final LongSupplier consumerLinger;
	protected final BiConsumer<Throwable, C> consumerThrowableHandler;
	protected final ScheduledExecutorService consumerScheduledExecutor;
//	protected final Lock consumerLock = new ReentrantLock();
//	protected final AtomicBoolean consumerRunning = new AtomicBoolean();

	public BufferTriggerImpl(Supplier<C> containerFactory, //
			ToIntBiFunction<C, E> containerEnqueue, //
			Lock enqueueLock, //
			Consumer<C> consumer, //
			LongSupplier consumerLinger, //
			BiConsumer<Throwable, C> consumerThrowableHandler, //
			ScheduledExecutorService consumerScheduledExecutor) {
		super();
		this.containerFactory = containerFactory;
		this.containerRef = new AtomicReference<>(new ContainerHolder());
		this.containerEnqueue = containerEnqueue;
		ReentrantReadWriteLock containerLock = new ReentrantReadWriteLock();
		this.containerRLock = containerLock.readLock();
		this.containerWLock = containerLock.writeLock();
		this.enqueueLock = enqueueLock;
		this.consumer = consumer;
		this.consumerLinger = consumerLinger;
		this.consumerThrowableHandler = consumerThrowableHandler;
		this.consumerScheduledExecutor = consumerScheduledExecutor;
		this.consumerScheduledExecutor.schedule(new ConsumerRunnable(), this.consumerLinger.getAsLong(),
				TimeUnit.MILLISECONDS);
	}

	@Override
	public void enqueue(E element) {
		LockUtils.runInLock(containerRLock,
				() -> LockUtils.runInLock(enqueueLock, () -> containerRef.get().accept(element)));
	}

	protected void consume() {
		ContainerHolder holder = LockUtils.runInLock(containerWLock, () -> {
			return containerRef.getAndSet(new ContainerHolder());
		});
		consume(holder);
	}

	protected final void consume(ContainerHolder holder) {
		if (holder.counter.get() <= 0) {
			return;
		}
		try {
			consumer.accept(holder.container);
		} catch (Throwable throwable) {
			consumerThrowableHandler.accept(throwable, holder.container);
		}
	}

	class ConsumerRunnable implements Runnable {
		@Override
		public void run() {
			try {
				consume();
			} finally {
				consumerScheduledExecutor.schedule(this, consumerLinger.getAsLong(), TimeUnit.MILLISECONDS);
			}
		}
	}

	class ContainerHolder {
		final C container;
		final AtomicLong counter = new AtomicLong(0L);

		public ContainerHolder() {
			this.container = containerFactory.get();
		}

		public void accept(E element) {
			counter.addAndGet(containerEnqueue.applyAsInt(container, element));
		}
	}

	@Override
	public void manuallyDoTrigger() {
		this.consume();
	}

	@Override
	public long getPendingChanges() {
		return this.containerRef.get().counter.get();
	}

	@Override
	public void close() {
		this.consume();
	}

}
