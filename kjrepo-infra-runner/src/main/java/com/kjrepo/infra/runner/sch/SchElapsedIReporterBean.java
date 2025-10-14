package com.kjrepo.infra.runner.sch;

import com.kjrepo.infra.reporter.bean.IReporterBean;

public class SchElapsedIReporterBean implements IReporterBean {

	private final SchRunner job;
	private final long elapsed;

	public SchElapsedIReporterBean(SchRunner job, long elapsed) {
		super();
		this.job = job;
		this.elapsed = elapsed;
	}

	public SchRunner job() {
		return job;
	}

	public long elapsed() {
		return elapsed;
	}

}
