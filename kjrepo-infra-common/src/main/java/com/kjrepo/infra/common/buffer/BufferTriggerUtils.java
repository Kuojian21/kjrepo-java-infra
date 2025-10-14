package com.kjrepo.infra.common.buffer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;

class BufferTriggerUtils {

	public static final Lock NOLOCK = new NoLock();

	public static <E> BufferTriggerRejectHandler<E> defaultRejectHandler() {
		return new DefaultRejectHandler<E>();
	}

	public static <T> T runInLock(Lock lock, Supplier<T> supplier) {
		lock.lock();
		try {
			return supplier.get();
		} finally {
			lock.unlock();
		}
	}

	public static void runInLock(Lock lock, Runnable runnable) {
		lock.lock();
		try {
			runnable.run();
		} finally {
			lock.unlock();
		}
	}

	public static void runInTryLock(Lock lock, Runnable runnable) {
		if (lock.tryLock()) {
			try {
				runnable.run();
			} finally {
				lock.unlock();
			}
		}
	}

	static class DefaultRejectHandler<E> implements BufferTriggerRejectHandler<E> {

		private static final Logger logger = LoggerUtils.logger();

		@Override
		public boolean onReject(E element, Condition condition) {
			logger.info("Reject Element:{}", element);
			return true;
		}

	}

	static class NoLock implements Lock {

		private NoLock() {
		}

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
	}

}
