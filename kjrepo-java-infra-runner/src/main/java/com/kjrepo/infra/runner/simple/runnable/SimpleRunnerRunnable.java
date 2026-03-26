package com.kjrepo.infra.runner.simple.runnable;

import java.util.concurrent.TimeUnit;

//import java.util.concurrent.CountDownLatch;

//import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.google.common.base.Stopwatch;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.common.trace.TraceIDUtils;
import com.kjrepo.infra.perf.utils.PerfUtils;
import com.kjrepo.infra.runner.simple.SimpleRunner;

public class SimpleRunnerRunnable implements Runnable {

	private final Logger logger = LoggerUtils.logger(this.getClass());
	private final SimpleRunner job;

	public SimpleRunnerRunnable(SimpleRunner job) {
		super();
		this.job = job;
	}

	@Override
	public void run() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		String jobID = this.job.module() + "." + this.job.ID();
		try {
			TraceIDUtils.generate();
			job.run();
			PerfUtils.perf(PerfUtils.N_runner_simple, "exec", jobID).count(1)
					.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
			logger.debug("job:" + jobID + " class:" + job.getClass().getName() + " elapsed:"
					+ N_humanUtils.formatMicros(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
		} catch (Exception e) {
			PerfUtils.perf(PerfUtils.N_runner_simple, e.getClass().getSimpleName(), jobID).count(1)
					.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
			logger.error("job:" + jobID + " class:" + job.getClass().getName() + " elapsed:"
					+ N_humanUtils.formatMicros(stopwatch.elapsed(TimeUnit.MICROSECONDS)), e);
		} finally {
			TraceIDUtils.clear();
		}
	}
}
