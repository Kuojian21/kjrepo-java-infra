package com.kjrepo.infra.runner.common;

import com.kjrepo.infra.register.context.RegisterContext;
import com.kjrepo.infra.register.context.RegisterFactory;
import com.kjrepo.infra.runner.Runner;

public abstract class RunnerHolder<R extends Runner> implements AutoCloseable {

	private final R runner;
	private final RegisterContext context;

	protected RunnerHolder(R runner) {
		this(runner, RegisterFactory.getContext(runner.getClass()));
	}

	protected RunnerHolder(R runner, RegisterContext context) {
		super();
		this.runner = runner;
		this.context = context;
	}

	public R runner() {
		return this.runner;
	}

	public RegisterContext context() {
		return this.context;
	}

}
