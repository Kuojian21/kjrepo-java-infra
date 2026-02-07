package com.kjrepo.infra.runner.sch.ksch;

import java.util.concurrent.TimeUnit;

//import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.slf4j.Logger;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Supplier;
import com.google.common.base.Stopwatch;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.common.trace.TraceIDUtils;
//import com.kjrepo.infra.distrib.lock.DLock;
//import com.kjrepo.infra.distrib.lock.context.DLockFactory;
import com.kjrepo.infra.reporter.utils.Reporter;
import com.kjrepo.infra.runner.sch.SchElapsedIReporterBean;

public class KschRunnerRunnable implements Runnable {

	private final Logger logger = LoggerUtils.logger(KschRunner.class);
	private final KschRunner job;

	public KschRunnerRunnable(KschRunner job) {
		super();
		this.job = job;
	}

	@Override
	public void run() {
//		DLock lock = DLockFactory.getContext(getClass())
//				.getLock(StringUtils.isEmpty(this.job.ID()) ? null : "/lock/ksch/" + this.job.ID());
		try {
//			lock.lock();
			while (!TermHelper.isStopping()) {
				TraceIDUtils.generate();
				Stopwatch stopwatch = Stopwatch.createStarted();
				Supplier<String> supplier = () -> new StringSubstitutor((StringLookup) key -> {
					switch (key) {
					case "group":
						return job.module();
					case "name":
						return Optional.ofNullable(job.ID()).orElse("");
					case "clazz":
						return job.getClass().getName();
					case "elapsed":
						return N_humanUtils.formatMills(stopwatch.elapsed(TimeUnit.MILLISECONDS));
					case "concurrent":
						return job.isConcurrentRunning() + "";
					default:
						return "";
					}
				}).replace("job:${group}.${name} concurrent:${concurrent} class:${clazz} elapsed:${elapsed}");
				long sleep = TimeUnit.SECONDS.toMillis(5);
				try {
					sleep = job.run();
					logger.debug(supplier.get());
				} catch (Exception e) {
					logger.error(supplier.get(), e);
				} finally {
					Reporter.report(new SchElapsedIReporterBean(job, stopwatch.elapsed(TimeUnit.MILLISECONDS)));
					TraceIDUtils.clear();
				}
//				Uninterruptibles.sleepUninterruptibly(sleep, TimeUnit.MILLISECONDS);
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					break;
				}
			}
		} finally {
//			lock.unlock();
		}
	}

}
