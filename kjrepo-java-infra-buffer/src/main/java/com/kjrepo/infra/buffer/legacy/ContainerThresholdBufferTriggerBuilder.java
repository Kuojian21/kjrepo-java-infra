//package com.kjrepo.infra.buffer.legacy;
//
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//import java.util.function.BiConsumer;
//import java.util.function.Consumer;
//import java.util.function.IntSupplier;
//import java.util.function.LongSupplier;
//import java.util.function.Supplier;
//import java.util.function.ToIntBiFunction;
//
//import org.slf4j.Logger;
//
//import com.github.phantomthief.collection.BufferTrigger;
//import com.google.common.util.concurrent.MoreExecutors;
//import com.google.common.util.concurrent.ThreadFactoryBuilder;
//import com.kjrepo.infra.common.logger.LoggerUtils;
//import com.kjrepo.infra.common.term.TermHelper;
//import com.kjrepo.infra.common.trace.TraceIDUtils;
//
//public class ContainerThresholdBufferTriggerBuilder<E, C> {
//
//	private final Logger logger = LoggerUtils.logger(getClass());
//
//	private Supplier<C> containerFactory;
//	private ToIntBiFunction<C, E> containerEnqueue;
//	private LongSupplier containerCapacity;
//	private ContainerThresholdBufferTriggerRejectHandler<E> containerRejectHandler;
//
//	private Lock enqueueLock;
//	private IntSupplier enqueueTriggerConsumeThreshold;
//
//	private Consumer<C> consumer;
//	private LongSupplier consumerLinger;
//	private BiConsumer<Throwable, C> consumerThrowableHandler;
//	private ScheduledExecutorService consumerScheduledExecutor;
//	private Executor consumerWorkerExecutor;
//
//	public ContainerThresholdBufferTriggerBuilder<E, C> setContainer(Supplier<C> containerFactory,
//			BiConsumer<C, E> containerEnqueue) {
//		this.containerFactory = containerFactory;
//		this.containerEnqueue = (c, e) -> {
//			containerEnqueue.accept(c, e);
//			return 1;
//		};
//		return this;
//	}
//
//	public ContainerThresholdBufferTriggerBuilder<E, C> setContainerEx(Supplier<C> containerFactory,
//			ToIntBiFunction<C, E> containerEnqueue) {
//		this.containerFactory = containerFactory;
//		this.containerEnqueue = containerEnqueue;
//		return this;
//	}
//
//	public ContainerThresholdBufferTriggerBuilder<E, C> setContainerCapacity(long containerCapacity) {
//		this.containerCapacity = () -> containerCapacity;
//		return this;
//	}
//
//	public ContainerThresholdBufferTriggerBuilder<E, C> setContainerRejectHandler(
//			ContainerThresholdBufferTriggerRejectHandler<E> containerRejectHandler) {
//		this.containerRejectHandler = containerRejectHandler;
//		return this;
//	}
//
//	public ContainerThresholdBufferTriggerBuilder<E, C> enableEnqueueLock() {
//		this.enqueueLock = new ReentrantLock();
//		return this;
//	}
//
//	public ContainerThresholdBufferTriggerBuilder<E, C> disableEnqueueLock() {
//		this.enqueueLock = NOLOCK;
//		return this;
//	}
//
//	public ContainerThresholdBufferTriggerBuilder<E, C> setEnqueueTriggerConsumeThreshold(
//			int enqueueTriggerConsumeThreshold) {
//		this.enqueueTriggerConsumeThreshold = () -> enqueueTriggerConsumeThreshold;
//		return this;
//	}
//
//	public ContainerThresholdBufferTriggerBuilder<E, C> setConsumer(Consumer<C> consumer) {
//		this.consumer = c -> {
//			String traceid = TraceIDUtils.get();
//			try {
//				TraceIDUtils.generate(traceid);
//				consumer.accept(c);
//			} finally {
//				TraceIDUtils.set(traceid);
//			}
//		};
//		return this;
//	}
//
//	public ContainerThresholdBufferTriggerBuilder<E, C> setConsumeLinger(long consumerLinger) {
//		this.consumerLinger = () -> consumerLinger;
//		return this;
//	}
//
//	public ContainerThresholdBufferTriggerBuilder<E, C> setConsumeThrowableHandler(
//			BiConsumer<Throwable, C> consumerThrowableHandler) {
//		this.consumerThrowableHandler = consumerThrowableHandler;
//		return this;
//	}
//
//	public ContainerThresholdBufferTriggerBuilder<E, C> setConsumeScheduledExecutor(
//			ScheduledExecutorService consumerScheduledExecutor) {
//		this.consumerScheduledExecutor = consumerScheduledExecutor;
//		return this;
//	}
//
//	public ContainerThresholdBufferTriggerBuilder<E, C> setConsumeWorkerExecutor(Executor consumerWorkerExecutor) {
//		this.consumerWorkerExecutor = consumerWorkerExecutor;
//		return this;
//	}
//
//	public BufferTrigger<E> build() {
//		ensure();
//		BufferTrigger<E> trigger = new ContainerThresholdBufferTriggerImpl<>(containerFactory, //
//				containerEnqueue, //
//				containerCapacity, //
//				containerRejectHandler, //
//				enqueueLock, //
//				enqueueTriggerConsumeThreshold, //
//				consumer, //
//				consumerLinger, //
//				consumerThrowableHandler, //
//				consumerScheduledExecutor, //
//				consumerWorkerExecutor);
//		TermHelper.addTerm("buffer-trigger", () -> {
//			trigger.manuallyDoTrigger();
//		});
//		return trigger;
//	}
//
//	public void ensure() {
//		if (containerFactory == null) {
//			throw new RuntimeException("does not set containerFactory!!");
//		}
//		if (containerEnqueue == null) {
//			throw new RuntimeException("does not set containerEnqueue!!");
//		}
//		if (containerCapacity == null) {
//			containerCapacity = () -> Long.MAX_VALUE;
//		}
//		if (containerRejectHandler == null) {
//			containerRejectHandler = new ContainerThresholdBufferTriggerRejectHandler<E>() {
//				@Override
//				public boolean onReject(E element, Condition condition) {
//					logger.info("Reject Element:{}", element);
//					return true;
//				}
//			};
//		}
//		if (enqueueLock == null) {
//			enqueueLock = new ReentrantLock();
//		}
//		if (enqueueTriggerConsumeThreshold == null) {
//			enqueueTriggerConsumeThreshold = () -> Integer.MAX_VALUE;
//		}
//		if (consumer == null) {
//			throw new RuntimeException("does not set consumer!!");
//		}
//		if (consumerLinger == null) {
//			consumerLinger = () -> 1000;
//		}
//		if (consumerThrowableHandler == null) {
//			consumerThrowableHandler = (throwable, container) -> logger.error("consume error, container:" + container,
//					throwable);
//		}
//		if (consumerScheduledExecutor == null) {
//			consumerScheduledExecutor = Executors.newSingleThreadScheduledExecutor(
//					new ThreadFactoryBuilder().setNameFormat("buffer-trigger-%d").setDaemon(true).build());
//		}
//		if (consumerWorkerExecutor == null) {
//			consumerWorkerExecutor = MoreExecutors.directExecutor();
//		}
//	}
//
//	private static final Lock NOLOCK = new Lock() {
//
//		@Override
//		public void lock() {
//			// Do nothing
//		}
//
//		@Override
//		public void unlock() {
//			// Do nothing
//		}
//
//		@Override
//		public void lockInterruptibly() throws InterruptedException {
//			throw new UnsupportedOperationException("Should not be called");
//		}
//
//		@Override
//		public boolean tryLock() {
//			throw new UnsupportedOperationException("Should not be called");
//		}
//
//		@Override
//		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
//			throw new UnsupportedOperationException("Should not be called");
//		}
//
//		@Override
//		public Condition newCondition() {
//			throw new UnsupportedOperationException("Should not be called");
//		}
//	};
//
//}
