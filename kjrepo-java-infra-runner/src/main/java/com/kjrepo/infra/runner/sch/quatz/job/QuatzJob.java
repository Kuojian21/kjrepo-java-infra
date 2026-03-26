package com.kjrepo.infra.runner.sch.quatz.job;

import java.util.concurrent.TimeUnit;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import com.google.common.base.Stopwatch;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.common.trace.TraceIDUtils;
import com.kjrepo.infra.perf.utils.PerfUtils;
import com.kjrepo.infra.runner.sch.quatz.QuatzRunner;

public class QuatzJob implements Job {

	private final Logger logger = LoggerUtils.logger(QuatzJob.class);
	private final QuatzRunner job;

	public QuatzJob(QuatzRunner job) {
		this.job = job;
	}

	@Override
	public final void execute(JobExecutionContext context) throws JobExecutionException {
		if (TermHelper.isStopping()) {
			return;
		}
		Stopwatch stopwatch = Stopwatch.createStarted();
		String jobID = job.module() + "." + job.ID();
		try {
			TraceIDUtils.generate();
			this.job.run();
			PerfUtils.perf(PerfUtils.N_runner_quatz, "exec", jobID).count(1)
					.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
			logger.debug("job:" + jobID + " class:" + job.getClass().getName() + " elapsed:"
					+ N_humanUtils.formatMicros(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
		} catch (Throwable e) {
			PerfUtils.perf(PerfUtils.N_runner_quatz, e.getClass().getSimpleName(), jobID).count(1)
					.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
			logger.error("job:" + jobID + " class:" + job.getClass().getName() + " elapsed:"
					+ N_humanUtils.formatMicros(stopwatch.elapsed(TimeUnit.MICROSECONDS)), e);
		} finally {
			TraceIDUtils.clear();
		}
	}

	public QuatzRunner getJob() {
		return job;
	}
}
