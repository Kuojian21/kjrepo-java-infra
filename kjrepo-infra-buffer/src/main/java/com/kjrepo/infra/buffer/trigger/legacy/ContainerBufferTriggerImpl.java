package com.kjrepo.infra.buffer.trigger.legacy;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import com.github.phantomthief.collection.BufferTrigger;
import com.github.phantomthief.util.MoreLocks;

class ContainerBufferTriggerImpl<E, C> implements BufferTrigger<E> {

	private final Supplier<C> containerFactory;
	private final AtomicReference<C> containerRef;
	private final BiConsumer<C, E> containerEnqueue;
	private final Function<C, Integer> containerLength;
	private final Lock containerRLock;
	private final Lock containerWLock;
	private final Lock enqueueLock;
	private final Consumer<C> consumer;
	private final LongSupplier consumeLinger;
	private final BiConsumer<Throwable, C> consumeThrowableHandler;
	private final ScheduledExecutorService consumeScheduledExecutor;
	private final Lock consumeLock = new ReentrantLock();
	private final AtomicBoolean comsumeRunning = new AtomicBoolean();

	public ContainerBufferTriggerImpl(Supplier<C> containerFactory, //
			BiConsumer<C, E> containerEnqueue, //
			Function<C, Integer> containerLength, //
			Lock enqueueLock, //
			Consumer<C> consumer, //
			LongSupplier comsumeLinger, //
			BiConsumer<Throwable, C> comsumeThrowableHandler, //
			ScheduledExecutorService consumeScheduledExecutor) {
		super();
		this.containerFactory = containerFactory;
		this.containerRef = new AtomicReference<>(this.containerFactory.get());
		this.containerEnqueue = containerEnqueue;
		this.containerLength = containerLength;
		ReentrantReadWriteLock containerLock = new ReentrantReadWriteLock();
		this.containerRLock = containerLock.readLock();
		this.containerWLock = containerLock.writeLock();
		this.enqueueLock = enqueueLock;
		this.consumer = consumer;
		this.consumeLinger = comsumeLinger;
		this.consumeThrowableHandler = comsumeThrowableHandler;
		this.consumeScheduledExecutor = consumeScheduledExecutor;
		this.consumeScheduledExecutor.schedule(new ConsumerRunnable(), this.consumeLinger.getAsLong(),
				TimeUnit.MILLISECONDS);
	}

	@Override
	public void enqueue(E element) {
		MoreLocks.runWithLock(containerRLock,
				() -> MoreLocks.runWithLock(enqueueLock, () -> containerEnqueue.accept(containerRef.get(), element)));
	}

	private void doConsume() {
		MoreLocks.runWithLock(consumeLock, () -> {
			try {
				comsumeRunning.set(true);
				C data = MoreLocks.supplyWithLock(containerWLock, () -> {
					return containerRef.getAndSet(this.containerFactory.get());
				});
				try {
					consumer.accept(data);
				} catch (Throwable throwable) {
					consumeThrowableHandler.accept(throwable, data);
				}
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

	@Override
	public void manuallyDoTrigger() {
		this.doConsume();
	}

	@Override
	public long getPendingChanges() {
		return this.containerLength.apply(this.containerRef.get());
	}

	@Override
	public void close() {
		this.doConsume();
	}

}
