package com.kjrepo.infra.runner.simple;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.trace.utils.TraceIDUtils;

public class SimpleRunnerRunnable implements Runnable {

	private final Logger logger = LoggerUtils.logger(this.getClass());
	private final CountDownLatch latch = new CountDownLatch(1);
	private final SimpleRunner runner;

	public SimpleRunnerRunnable(SimpleRunner runner) {
		super();
		this.runner = runner;
	}

	@Override
	public void run() {
		TermHelper.addTerm(runner.module(), () -> {
			latch.await();
		});
		TraceIDUtils.generate();
		try {
			runner.run();
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			TraceIDUtils.clear();
		}
		latch.countDown();
	}

}
