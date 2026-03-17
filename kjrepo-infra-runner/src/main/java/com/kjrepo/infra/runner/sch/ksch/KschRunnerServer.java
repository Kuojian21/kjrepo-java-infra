package com.kjrepo.infra.runner.sch.ksch;

import java.util.concurrent.atomic.AtomicInteger;

import com.annimon.stream.Optional;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;

public class KschRunnerServer extends AbstractRunnerServer<KschRunner> {

	private static final AtomicInteger number = new AtomicInteger(0);

	@Override
	protected void doRun(KschRunner runner) {
		Thread thread = new Thread(new KschRunnerRunnable(runner));
		thread.setName(Optional.ofNullable(runner.module()).orElseGet(() -> "ksch-thread-" + number.incrementAndGet()));
		thread.setDaemon(false);
		thread.start();
		TermHelper.addTerm("ksch-" + runner.module(), () -> {
			thread.interrupt();
			thread.join();
		});
	}

	@Override
	protected boolean nlock() {
		return true;
	}

}
