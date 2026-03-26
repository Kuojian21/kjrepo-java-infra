package com.kjrepo.infra.runner.sch.quatz.detail;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;

import com.kjrepo.infra.runner.sch.quatz.job.QuatzJob;

public class QuatzJobDetailBuilder extends JobBuilder {

	public static <T extends QuatzJob> QuatzJobDetailBuilder job(T job) {
		return new QuatzJobDetailBuilder(job);
	}

	private final QuatzJob job;
	private Object[] args;

	private QuatzJobDetailBuilder(QuatzJob job) {
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
