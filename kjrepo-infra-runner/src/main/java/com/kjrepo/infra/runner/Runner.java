package com.kjrepo.infra.runner;

import com.kjrepo.infra.runner.server.RunnerServerFactory;
import com.kjrepo.infra.runner.utils.RunnerUtils;

public interface Runner {

	default String module() {
		return this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.') + 1);
	}

	default String ID() {
		return null;
	}

	default <R extends Runner> void execute() {
		RunnerUtils.run(RunnerServerFactory.server(this.getClass()), this);
	}

}
