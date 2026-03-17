package com.kjrepo.infra.monitor.mx;

import com.kjrepo.infra.monitor.mx.bean.OperatingSystemReporterBean;
import com.kjrepo.infra.reporter.utils.Reporter;
import com.sun.management.OperatingSystemMXBean;

public class OperatingSystemMonitor extends AbstractMonitor<OperatingSystemMXBean> {

	@Override
	public void monitor() {
		Reporter.report(new OperatingSystemReporterBean(beans()));
	}

}
