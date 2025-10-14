package com.kjrepo.infra.common.lazy;

import java.util.concurrent.Callable;

public class LazyCallable<T> implements Callable<T> {

	public static <T> LazyCallable<T> wrap(Callable<T> callbable) {
		return new LazyCallable<T>(callbable);
	}

	private final Callable<T> delegate;
	private volatile boolean inited = false;
	private volatile T value;

	public LazyCallable(Callable<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public T call() throws Exception {
		if (!this.inited) {
			synchronized (this) {
				if (!this.inited) {
					this.value = this.delegate.call();
					this.inited = true;
				}
			}
		}
		return this.value;
	}

	public void refresh() {
		this.inited = false;
	}

	public boolean isInited() {
		return this.inited;
	}

}
