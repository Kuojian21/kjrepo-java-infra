package com.kjrepo.infra.monitor.mx;

import java.lang.management.ClassLoadingMXBean;

import com.kjrepo.infra.monitor.mx.bean.ClassLoadingReportBean;
import com.kjrepo.infra.reporter.utils.Reporter;

public class ClassLoadingMonitor extends AbstractMonitor<ClassLoadingMXBean> {

	@Override
	public void monitor() {
		Reporter.report(new ClassLoadingReportBean(beans()));
	}

}
