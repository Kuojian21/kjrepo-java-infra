package com.kjrepo.infra.runner.sch.quatz;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;

public class QuatzJobDetailBuilder extends JobBuilder {

	public static <T extends Job> QuatzJobDetailBuilder job(T job) {
		return new QuatzJobDetailBuilder(job);
	}

	private final Job job;
	private Object[] args;

	private QuatzJobDetailBuilder(Job job) {
		super();
		this.job = job;
		this.ofType(job.getClass());
	}

	public QuatzJobDetailBuilder args(Object[] args) {
		this.args = args;
		return this;
	}

	@Override
	public JobDetail build() {
		return new QuatzJobDetail(super.build(), this.job, this.args);
	}
}
