package com.kjrepo.infra.runner.sch.quatz;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import com.kjrepo.infra.common.lazy.LazySupplier;

public class QuatzJobFactory implements JobFactory {

	private LazySupplier<JobFactory> supplier = LazySupplier.<JobFactory>wrap(() -> new SimpleJobFactory());

	@Override
	public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
		JobDetail jobDetail = bundle.getJobDetail();
		if (jobDetail instanceof QuatzJobDetail) {
			Job job = ((QuatzJobDetail) jobDetail).getJob();
			if (job == null) {
				return supplier.get().newJob(bundle, scheduler);
			} else {
				return job;
			}
		} else {
			return supplier.get().newJob(bundle, scheduler);
		}
	}
}
