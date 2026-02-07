package com.kjrepo.infra.runner.sch.quatz;

import java.util.concurrent.TimeUnit;

import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Supplier;
import com.google.common.base.Stopwatch;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.common.trace.TraceIDUtils;
import com.kjrepo.infra.reporter.utils.Reporter;
import com.kjrepo.infra.runner.sch.SchElapsedIReporterBean;

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
		Supplier<String> supplier = () -> new StringSubstitutor((StringLookup)key -> {
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
		try {
			TraceIDUtils.generate();
			this.job.run();
			logger.debug(supplier.get());
		} catch (Throwable e) {
			logger.error(supplier.get(), e);
		} finally {
			Reporter.report(new SchElapsedIReporterBean(job, stopwatch.elapsed(TimeUnit.MILLISECONDS)));
			TraceIDUtils.clear();
		}
//		}
	}

	public QuatzRunner getJob() {
		return job;
	}
}
