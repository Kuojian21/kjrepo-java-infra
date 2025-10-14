package com.kjrepo.infra.common.executor;

public abstract class InfoExecutor<T, I> extends Executor<T> {

	private final I info;

	public InfoExecutor(I info) {
		this.info = info;
	}

	public I info() {
		return info;
	}

}
