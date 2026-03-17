package com.kjrepo.infra.monitor.mx;

import com.kjrepo.infra.monitor.mx.bean.OperatingSystemReporterUnixBean;
import com.kjrepo.infra.reporter.utils.Reporter;
import com.sun.management.UnixOperatingSystemMXBean;

public class OperatingSystemUnixMonitor extends AbstractMonitor<UnixOperatingSystemMXBean> {

	@Override
	public void monitor() {
		Reporter.report(new OperatingSystemReporterUnixBean(beans()));
	}

}
