package com.kjrepo.infra.monitor.mx;

import java.lang.management.CompilationMXBean;

import com.kjrepo.infra.monitor.mx.bean.CompilationReportBean;
import com.kjrepo.infra.reporter.utils.Reporter;

public class CompilationMonitor extends AbstractMonitor<CompilationMXBean> {

	@Override
	public void monitor() {
		Reporter.report(new CompilationReportBean(beans()));
	}

}
