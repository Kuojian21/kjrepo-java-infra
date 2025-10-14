package com.kjrepo.infra.monitor.mx;

import com.kjrepo.infra.monitor.mx.reporter.OperatingSystemIReporterBean;
import com.kjrepo.infra.reporter.utils.Reporter;
import com.sun.management.OperatingSystemMXBean;

public class OperatingSystemMonitor2 extends AbstractMonitor<OperatingSystemMXBean> {

	@Override
	public void monitor() {
		Reporter.report(new OperatingSystemIReporterBean(bean()));
	}

}
