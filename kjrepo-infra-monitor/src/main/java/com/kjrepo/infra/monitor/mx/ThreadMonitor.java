package com.kjrepo.infra.monitor.mx;

import java.lang.management.ThreadMXBean;

import com.kjrepo.infra.monitor.mx.reporter.ThreadIReporterBean;
import com.kjrepo.infra.reporter.utils.Reporter;

public class ThreadMonitor extends AbstractMonitor<ThreadMXBean> {

	@Override
	public void monitor() {
		Reporter.report(new ThreadIReporterBean(bean()/* ManagementFactory.getThreadMXBean() */));
	}

}
