package com.kjrepo.infra.common.lazy;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import java.util.concurrent.locks.Lock;

import com.annimon.stream.function.Supplier;
import com.annimon.stream.function.ThrowableConsumer;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class LazySupplier<T>
		implements Supplier<T>, com.google.common.base.Supplier<T>, java.util.function.Supplier<T> {

	public static <T> LazySupplier<T> wrap(Supplier<T> supplier) {
		return new LazySupplier<T>(supplier);
	}

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock rLock = lock.readLock();
	private final Lock wLock = lock.writeLock();
	private final Supplier<T> delegate;
	private volatile boolean inited = false;
	private volatile T value;

	public LazySupplier(Supplier<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public T get() {
		if (!this.inited) {
			try {
				wLock.lock();
				if (!this.inited) {
					this.value = this.delegate.get();
					this.inited = true;
				}
			} finally {
				wLock.unlock();
			}
		}
		return this.value;
	}

	public void refresh() {
		refresh(null);
	}

	public <E extends Throwable> void refresh(ThrowableConsumer<T, E> release) {
		T oValue = null;
		try {
			wLock.lock();
			if (this.inited) {
				oValue = this.value;
				this.inited = false;
			}
		} finally {
			wLock.unlock();
		}
		if (release != null && oValue != null) {
			try {
				release.accept(oValue);
			} catch (Throwable e) {
				LoggerUtils.logger(LazySupplier.class).error("", e);
			}
		}
	}

	public <E extends Throwable> void acceptIfInitedInLock(ThrowableConsumer<T, E> consumer) throws E {
		try {
			wLock.lock();
			if (this.inited) {
				consumer.accept(this.value);
			}
		} finally {
			wLock.unlock();
		}
	}

	public boolean isInited() {
		rLock.lock();
		try {
			return this.inited;
		} finally {
			rLock.unlock();
		}
	}

}