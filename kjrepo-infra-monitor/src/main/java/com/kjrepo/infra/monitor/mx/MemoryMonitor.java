package com.kjrepo.infra.monitor.mx;

import java.lang.management.MemoryMXBean;

import com.kjrepo.infra.monitor.mx.reporter.MemoryIReporterBean;
import com.kjrepo.infra.reporter.utils.Reporter;

public class MemoryMonitor extends AbstractMonitor<MemoryMXBean> {

	@Override
	public void monitor() {
		Reporter.report(new MemoryIReporterBean(bean()));
	}

}
