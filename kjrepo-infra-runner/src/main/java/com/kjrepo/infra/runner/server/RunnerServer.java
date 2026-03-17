package com.kjrepo.infra.runner.server;

import com.kjrepo.infra.runner.Runner;

public interface RunnerServer<R extends Runner> {

	RunnerServer<R> run(R runner);

}
