package com.kjrepo.infra.common.lazy;

import java.util.function.Consumer;

import com.annimon.stream.Optional;

public class LazyConsumer<T> implements Consumer<T> {

	public static <T> LazyConsumer<T> wrap(Consumer<T> consumer) {
		return new LazyConsumer<T>(consumer);
	}

	private final Consumer<T> delegate;
	private volatile boolean inited = false;

	public LazyConsumer(Consumer<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public void accept(T t) {
		accept(t, null);
	}

	public void accept(T t, Runnable prompt) {
		if (!this.inited) {
			synchronized (this) {
				if (!this.inited) {
					this.delegate.accept(t);
					this.inited = true;
				} else {
					Optional.ofNullable(prompt).ifPresent(p -> p.run());
				}
			}
		} else {
			Optional.ofNullable(prompt).ifPresent(p -> p.run());
		}
	}

	public void refresh() {
		this.inited = false;
	}

	public boolean isInited() {
		return this.inited;
	}

}
