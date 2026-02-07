package com.kjrepo.infra.runner.simple;

//import java.util.List;

import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;

public class SimpleRunnerServer extends AbstractRunnerServer<SimpleRunner> {

	@Override
	protected void doRun(SimpleRunner runner) {
//		runners.forEach(runner -> {
		Thread thread = new Thread(new SimpleRunnerRunnable(runner));
		thread.start();
		TermHelper.addTerm(runner.module(), () -> thread.join());
//		});
//		return this;
	}
	
	@Override
	protected boolean nlock() {
		return true;
	}

}
