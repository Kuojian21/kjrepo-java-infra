package com.kjrepo.infra.runner.sch.quatz;

import org.quartz.DisallowConcurrentExecution;

@DisallowConcurrentExecution
public class QuatzJobConcurrentDisallow extends QuatzJob {

	public QuatzJobConcurrentDisallow(QuatzRunner job) {
		super(job);
	}

}
