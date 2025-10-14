package com.kjrepo.infra.runner.sch.ksch;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.annimon.stream.Optional;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;

public class KschRunnerServer extends AbstractRunnerServer<KschRunner> {

	private static final AtomicInteger number = new AtomicInteger(0);

	@Override
	public KschRunnerServer run(List<KschRunner> runners) {
		runners.forEach(runner -> {
			Thread thread = new Thread(new KschRunnerRunnable(runner));
			thread.setName(Optional.ofNullable(runner.ID()).orElseGet(() -> "ksch-thread-" + number.incrementAndGet()));
			thread.setDaemon(false);
			thread.start();
		});
		return this;
	}

}
