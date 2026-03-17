package com.kjrepo.infra.monitor.mx;

import java.lang.management.MemoryMXBean;

import com.kjrepo.infra.monitor.mx.bean.MemoryReporterBean;
import com.kjrepo.infra.reporter.utils.Reporter;

public class MemoryMonitor extends AbstractMonitor<MemoryMXBean> {

	@Override
	public void monitor() {
		Reporter.report(new MemoryReporterBean(beans()));
	}

}
