package com.kjrepo.infra.runner.sch.quatz.job;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.trace.TraceIDUtils;

public class QuatzJobStat implements Job {

	private final Logger logger = LoggerUtils.logger(getClass());

	public static final String GROUP = "kjrepo-infra";
	public static final String NAME = "stat";

	public QuatzJobStat() {
		super();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TraceIDUtils.generate();
		try {
			Scheduler scheduler = context.getScheduler();
			scheduler.getCurrentlyExecutingJobs().forEach(cejcontext -> {
				JobDetail jobDetail = cejcontext.getJobDetail();
				if (GROUP.equals(jobDetail.getKey().getGroup()) && NAME.equals(jobDetail.getKey().getName())) {
					return;
				}
				String job = jobDetail.getKey().getGroup() + "." + jobDetail.getKey().getName();
				logger.info("job {} is running", job);
			});
		} catch (SchedulerException e) {
			logger.error("", e);
		} finally {
			TraceIDUtils.clear();
		}
	}

}
