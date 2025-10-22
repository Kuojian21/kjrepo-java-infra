package com.kjrepo.infra.common.lazy;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

import com.annimon.stream.function.Supplier;

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
			wLock.lock();
			try {
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
		this.inited = false;
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