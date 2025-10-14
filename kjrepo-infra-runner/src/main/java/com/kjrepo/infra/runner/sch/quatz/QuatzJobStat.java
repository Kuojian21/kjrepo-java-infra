package com.kjrepo.infra.runner.sch.quatz;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.trace.utils.TraceIDUtils;

public class QuatzJobStat implements Job {

	private final Logger logger = LoggerUtils.logger();

	private final Scheduler scheduler;

	public QuatzJobStat(Scheduler scheduler) {
		super();
		this.scheduler = scheduler;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TraceIDUtils.generate();
		try {
			scheduler.getCurrentlyExecutingJobs().forEach(schContext -> {
				JobDetail jobDetail = schContext.getJobDetail();
				if (group().equals(jobDetail.getKey().getGroup()) && name().equals(jobDetail.getKey().getName())) {
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

	public String group() {
		return "kjrepo";
	}

	public String name() {
		return "stat-job";
	}

}
