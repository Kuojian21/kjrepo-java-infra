package com.kjrepo.infra.monitor.mx;

import java.lang.management.MemoryPoolMXBean;

import com.kjrepo.infra.monitor.mx.reporter.MemoryPoolIReporterBean;
import com.kjrepo.infra.reporter.utils.Reporter;

public class MemoryPoolMonitor extends AbstractMonitor<MemoryPoolMXBean> {

	@Override
	public void monitor() {
		Reporter.report(new MemoryPoolIReporterBean(beans()));
	}

}
