package com.kjrepo.infra.runner.legacy;

import org.quartz.DisallowConcurrentExecution;

import com.kjrepo.infra.runner.sch.quatz.QuatzJob;
import com.kjrepo.infra.runner.sch.quatz.QuatzRunner;

@DisallowConcurrentExecution
public class QuatzJobConcurrentDisallow extends QuatzJob {

	public QuatzJobConcurrentDisallow(QuatzRunner job) {
		super(job);
	}

}
