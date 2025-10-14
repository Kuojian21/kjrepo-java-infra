package com.kjrepo.infra.monitor.mx;

import java.lang.management.OperatingSystemMXBean;

import com.kjrepo.infra.monitor.mx.reporter.OperatingSystemIReporterBean;
import com.kjrepo.infra.reporter.utils.Reporter;

public class OperatingSystemMonitor extends AbstractMonitor<OperatingSystemMXBean> {

	@Override
	public void monitor() {
		Reporter.report(new OperatingSystemIReporterBean(bean()));
	}

}
