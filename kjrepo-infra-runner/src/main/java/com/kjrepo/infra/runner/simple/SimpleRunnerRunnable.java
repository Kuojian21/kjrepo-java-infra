package com.kjrepo.infra.runner.simple;

//import java.util.concurrent.CountDownLatch;

//import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.trace.TraceIDUtils;

public class SimpleRunnerRunnable implements Runnable {

	private final Logger logger = LoggerUtils.logger(this.getClass());
	private final SimpleRunner runner;

	public SimpleRunnerRunnable(SimpleRunner runner) {
		super();
		this.runner = runner;
	}

	@Override
	public void run() {
//		DLock lock = DLockFactory.getContext(getClass())
//				.getLock(StringUtils.isEmpty(this.runner.ID()) ? null : "/lock/simple/" + this.runner.ID());
//		lock.lock();
//		TermHelper.addTerm(runner.module(), () -> lock.unlock());
		try {
			TraceIDUtils.generate();
			runner.run();
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			TraceIDUtils.clear();
		}
	}

}
