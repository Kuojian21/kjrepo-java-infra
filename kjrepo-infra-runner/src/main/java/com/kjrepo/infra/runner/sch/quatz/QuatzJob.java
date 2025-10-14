package com.kjrepo.infra.runner.sch.quatz;

import java.util.concurrent.TimeUnit;

import org.apache.commons.text.StringSubstitutor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Supplier;
import com.google.common.base.Stopwatch;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.utils.HumanUtils;
import com.kjrepo.infra.reporter.utils.Reporter;
import com.kjrepo.infra.runner.sch.SchElapsedIReporterBean;
import com.kjrepo.infra.trace.utils.TraceIDUtils;

public class QuatzJob implements Job {

	private final Logger logger = LoggerUtils.logger();
	private final QuatzRunner job;

	public QuatzJob(QuatzRunner job) {
		this.job = job;
	}

	@Override
	public final void execute(JobExecutionContext context) throws JobExecutionException {
		TraceIDUtils.generate();
		Stopwatch stopwatch = Stopwatch.createStarted();
		Supplier<String> supplier = () -> new StringSubstitutor(key -> {
			switch (key) {
			case "group":
				return job.module();
			case "name":
				return Optional.ofNullable(job.ID()).orElse("");
			case "clazz":
				return job.getClass().getName();
			case "elapsed":
				return HumanUtils.formatMills(stopwatch.elapsed(TimeUnit.MILLISECONDS));
			case "concurrent":
				return job.isConcurrentRunning() + "";
			default:
				return "";
			}
		}).replace("job:${group}.${name} concurrent:${concurrent} class:${clazz} elapsed:${elapsed}");
		try {
			this.job.run();
			logger.info(supplier.get());
		} catch (Throwable e) {
			logger.error(supplier.get(), e);
		} finally {
			Reporter.report(new SchElapsedIReporterBean(job, stopwatch.elapsed(TimeUnit.MILLISECONDS)));
			TraceIDUtils.clear();
		}
	}
}
