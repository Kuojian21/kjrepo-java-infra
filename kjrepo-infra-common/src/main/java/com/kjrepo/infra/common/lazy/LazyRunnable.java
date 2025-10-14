package com.kjrepo.infra.common.lazy;

import com.annimon.stream.Optional;

public class LazyRunnable implements Runnable {

	public static LazyRunnable wrap(Runnable runnable) {
		return new LazyRunnable(runnable);
	}

	private final Runnable runnable;
	private volatile boolean inited = false;

	public LazyRunnable(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public void run() {
		run(null);
	}

	public void run(Runnable prompt) {
		if (!this.inited) {
			synchronized (this) {
				if (!this.inited) {
					this.runnable.run();
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
