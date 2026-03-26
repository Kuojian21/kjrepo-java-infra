//package com.kjrepo.infra.buffer.legacy;
//
//import java.util.concurrent.Executor;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//import java.util.function.BiConsumer;
//import java.util.function.Consumer;
//import java.util.function.IntSupplier;
//import java.util.function.LongSupplier;
//import java.util.function.Supplier;
//import java.util.function.ToIntBiFunction;
//
//import com.github.phantomthief.collection.BufferTrigger;
//import com.github.phantomthief.util.MoreLocks;
//
//public class ContainerThresholdBufferTriggerImpl<E, C> implements BufferTrigger<E> {
//
//	private final Supplier<C> containerFactory;
//	private final AtomicReference<ContainerHolder> containerHolder;
//	private final ToIntBiFunction<C, E> containerEnqueue;
//	private final LongSupplier containerCapacity;
//	private final ContainerThresholdBufferTriggerRejectHandler<E> containerRejectHandler;
//	private final Lock containerRLock;
//	private final Lock containerWLock;
//	private final Condition containerWCondition;
//
//	private final Lock enqueueLock;
//	private final IntSupplier enqueueTriggerConsumeThreshold;
//
//	private final Consumer<C> consumer;
//	private final LongSupplier consumerLinger;
//	private final BiConsumer<Throwable, C> consumerThrowableHandler;
//	private final ScheduledExecutorService consumerScheduledExecutor;
//	private final Executor consumerWorkerExecutor;
//	private final Lock consumerLock = new ReentrantLock();
//	private final AtomicBoolean consumerRunning = new AtomicBoolean();
//
//	public ContainerThresholdBufferTriggerImpl(Supplier<C> containerFactory, //
//			ToIntBiFunction<C, E> containerEnqueue, //
//			LongSupplier containerCapacity, //
//			ContainerThresholdBufferTriggerRejectHandler<E> containerRejectHandler, //
//			Lock enqueueLock, //
//			IntSupplier enqueueTriggerConsumeThreshold, //
//			Consumer<C> consumer, //
//			LongSupplier comsumeLinger, //
//			BiConsumer<Throwable, C> comsumeThrowableHandler, //
//			ScheduledExecutorService consumeScheduledExecutor, //
//			Executor consumeWorkerExecutor) {
//		super();
//		this.containerFactory = containerFactory;
//		this.containerHolder = new AtomicReference<>(new ContainerHolder());
//		this.containerEnqueue = containerEnqueue;
//		this.containerCapacity = containerCapacity;
//		this.containerRejectHandler = containerRejectHandler;
//		ReentrantReadWriteLock containerLock = new ReentrantReadWriteLock();
//		this.containerRLock = containerLock.readLock();
//		this.containerWLock = containerLock.writeLock();
//		this.containerWCondition = this.containerWLock.newCondition();
//		this.enqueueLock = enqueueLock;
//		this.enqueueTriggerConsumeThreshold = enqueueTriggerConsumeThreshold;
//		this.consumer = consumer;
//		this.consumerLinger = comsumeLinger;
//		this.consumerThrowableHandler = comsumeThrowableHandler;
//		this.consumerScheduledExecutor = consumeScheduledExecutor;
//		this.consumerWorkerExecutor = consumeWorkerExecutor;
//		this.consumerScheduledExecutor.schedule(new ConsumerRunnable(), this.consumerLinger.getAsLong(),
//				TimeUnit.MILLISECONDS);
//	}
//
//	@Override
//	public void enqueue(E element) {
//		if (containerHolder.get().counter.get() >= containerCapacity.getAsLong()) {
//			if (MoreLocks.supplyWithLock(containerWLock, () -> {
//				ContainerHolder holder = containerHolder.get();
//				if (holder.counter.get() >= containerCapacity.getAsLong()) {
//					return containerRejectHandler.onReject(element, containerWCondition);
//				} else {
//					return false;
//				}
//			})) {
//				return;
//			}
//		}
//		MoreLocks.runWithLock(containerRLock, () -> {
//			ContainerHolder holder = containerHolder.get();
//			holder.counter.addAndGet(MoreLocks.supplyWithLock(enqueueLock,
//					() -> containerEnqueue.applyAsInt(holder.container, element)));
//		});
//		if (containerHolder.get().counter.get() >= enqueueTriggerConsumeThreshold.getAsInt()) {
//			MoreLocks.runWithTryLock(consumerLock, () -> {
//				if (containerHolder.get().counter.get() >= enqueueTriggerConsumeThreshold.getAsInt()
//						&& !consumerRunning.get()) {
//					consumerRunning.set(true);
//					consumerScheduledExecutor.execute(() -> doConsume());
//				}
//			});
//		}
//	}
//
//	private void doConsume() {
//		MoreLocks.runWithLock(consumerLock, () -> {
//			try {
//				consumerRunning.set(true);
//				C data = MoreLocks.supplyWithLock(containerWLock, () -> {
//					C container = containerHolder.getAndSet(new ContainerHolder()).container;
//					containerWCondition.signalAll();
//					return container;
//				});
//				this.consumerWorkerExecutor.execute(() -> {
//					try {
//						consumer.accept(data);
//					} catch (Throwable throwable) {
//						consumerThrowableHandler.accept(throwable, data);
//					}
//				});
//			} finally {
//				consumerRunning.set(false);
//			}
//		});
//	}
//
//	class ConsumerRunnable implements Runnable {
//		@Override
//		public void run() {
//			try {
//				doConsume();
//			} finally {
//				consumerScheduledExecutor.schedule(this, consumerLinger.getAsLong(), TimeUnit.MILLISECONDS);
//			}
//		}
//	}
//
//	class ContainerHolder {
//		final C container;
//		final AtomicLong counter = new AtomicLong(0L);
//
//		public ContainerHolder() {
//			super();
//			this.container = containerFactory.get();
//		}
//	}
//
//	@Override
//	public void manuallyDoTrigger() {
//		this.doConsume();
//	}
//
//	@Override
//	public long getPendingChanges() {
//		return this.containerHolder.get().counter.get();
//	}
//
//	@Override
//	public void close() {
//		this.doConsume();
//	}
//
//}
