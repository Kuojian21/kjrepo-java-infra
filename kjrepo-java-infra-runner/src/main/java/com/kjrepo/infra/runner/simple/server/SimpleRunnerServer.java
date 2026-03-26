package com.kjrepo.infra.runner.simple.server;

import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;
import com.kjrepo.infra.runner.simple.SimpleRunner;
import com.kjrepo.infra.runner.simple.runnable.SimpleRunnerRunnable;

public class SimpleRunnerServer extends AbstractRunnerServer<SimpleRunner> {

	@Override
	protected void doRun(SimpleRunner runner) {
		Thread thread = new Thread(new SimpleRunnerRunnable(runner));
		thread.start();
		TermHelper.addTerm(runner.module(), () -> thread.join());
	}

	@Override
	protected boolean nlock() {
		return true;
	}

}
