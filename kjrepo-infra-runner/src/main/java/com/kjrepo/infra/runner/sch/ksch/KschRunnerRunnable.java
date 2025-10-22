package com.kjrepo.infra.runner.sch.ksch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.Uninterruptibles;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.distrib.lock.DLock;
import com.kjrepo.infra.distrib.lock.context.DLockFactory;
import com.kjrepo.infra.reporter.utils.Reporter;
import com.kjrepo.infra.runner.sch.SchElapsedIReporterBean;
import com.kjrepo.infra.trace.utils.TraceIDUtils;

public class KschRunnerRunnable implements Runnable {

	private final Logger logger = LoggerUtils.logger(this.getClass());
//	private final AtomicBoolean stoped = new AtomicBoolean(false);
	private final CountDownLatch latch = new CountDownLatch(1);
	private final KschRunner job;

	public KschRunnerRunnable(KschRunner job) {
		super();
		this.job = job;
	}

	@Override
	public void run() {
		DLock lock = DLockFactory.getContext(getClass())
				.getLock(StringUtils.isEmpty(this.job.ID()) ? null : "/lock/ksch/" + this.job.ID());
		try {
			lock.lock();
			TermHelper.addTerm(job.module(), () -> {
//				stoped.set(true);
				latch.await();
			});
			while (!TermHelper.isStopping()) {
				TraceIDUtils.generate();
				Stopwatch stopwatch = Stopwatch.createStarted();
				long sleep = TimeUnit.SECONDS.toMillis(5);
				try {
					sleep = job.run();
				} catch (Exception e) {
					logger.error("", e);
				} finally {
					Reporter.report(new SchElapsedIReporterBean(job, stopwatch.elapsed(TimeUnit.MILLISECONDS)));
					TraceIDUtils.clear();
				}
				Uninterruptibles.sleepUninterruptibly(sleep, TimeUnit.MILLISECONDS);
			}
		} finally {
			latch.countDown();
			lock.unlock();
		}
	}

}
