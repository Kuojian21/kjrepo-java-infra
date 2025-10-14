package com.kjrepo.infra.common.lazy;

import java.util.function.Function;

import com.annimon.stream.Optional;

public class LazyFunction<T, R> implements Function<T, R> {

	public static <T, R> LazyFunction<T, R> wrap(Function<T, R> function) {
		return new LazyFunction<T, R>(function);
	}

	private final Function<T, R> delegate;
	private volatile boolean inited = false;
	private volatile R value;

	public LazyFunction(Function<T, R> delegate) {
		this.delegate = delegate;
	}

	@Override
	public R apply(T t) {
		return apply(t, null);
	}

	public R apply(T t, Runnable prompt) {
		if (!this.inited) {
			synchronized (this) {
				if (!this.inited) {
					this.value = this.delegate.apply(t);
					this.inited = true;
				} else {
					Optional.ofNullable(prompt).ifPresent(p -> p.run());
				}
			}
		} else {
			Optional.ofNullable(prompt).ifPresent(p -> p.run());
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
