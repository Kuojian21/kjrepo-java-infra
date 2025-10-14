package com.kjrepo.infra.monitor.mx;

import java.lang.management.RuntimeMXBean;
import com.kjrepo.infra.monitor.mx.reporter.RuntimeIReporterBean;
import com.kjrepo.infra.reporter.utils.Reporter;

public class RuntimeMonitor extends AbstractMonitor<RuntimeMXBean> {

	@Override
	public void monitor() {
		Reporter.report(new RuntimeIReporterBean(bean()));
	}

}
