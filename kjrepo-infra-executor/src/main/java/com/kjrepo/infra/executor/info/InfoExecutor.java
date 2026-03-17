package com.kjrepo.infra.executor.info;

import com.kjrepo.infra.executor.Executor;

public abstract class InfoExecutor<T, I> extends Executor<T> {

	private final I info;

	public InfoExecutor(I info) {
		this.info = info;
	}

	public I info() {
		return info;
	}

}
