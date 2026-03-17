package com.kjrepo.infra.monitor.mx.bean;

import java.lang.management.CompilationMXBean;
import java.util.List;

public class CompilationReportBean extends AbstractReporterBean<CompilationMXBean> {

	public CompilationReportBean(List<CompilationMXBean> data) {
		super(data);
	}

}
