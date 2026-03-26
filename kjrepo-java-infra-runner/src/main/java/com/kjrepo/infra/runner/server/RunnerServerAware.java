package com.kjrepo.infra.runner.server;

import com.kjrepo.infra.runner.Runner;

public interface RunnerServerAware {

	@SuppressWarnings("unchecked")
	default <R extends Runner> RunnerServer<R> server() {
		try {
			return (RunnerServer<R>) Class.forName(this.getClass().getName().replaceAll("Aware$", ""))
					.getConstructor(new Class<?>[] {}).newInstance(new Object[] {});
		} catch (Throwable e) {
			return null;
		}
	}

}
