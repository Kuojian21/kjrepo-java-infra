package com.kjrepo.infra.runner.legacy;

import org.quartz.DisallowConcurrentExecution;

import com.kjrepo.infra.runner.sch.quatz.QuatzRunner;
import com.kjrepo.infra.runner.sch.quatz.job.QuatzJob;

@DisallowConcurrentExecution
public class QuatzJobConcurrentDisallow extends QuatzJob {

	public QuatzJobConcurrentDisallow(QuatzRunner job) {
		super(job);
	}

}
