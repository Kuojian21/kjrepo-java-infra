package com.kjrepo.infra.runner;

import com.kjrepo.infra.runner.server.RunnerServer;
import com.kjrepo.infra.runner.server.RunnerServerFactory;

public interface Runner {

	default String module() {
		return this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.') + 1);
	}

	default String ID() {
		return null;
	}

	@SuppressWarnings("unchecked")
	default <R extends Runner> void execute() {
		((RunnerServer<R>) RunnerServerFactory.server(this.getClass())).run((R) this);
	}

}
