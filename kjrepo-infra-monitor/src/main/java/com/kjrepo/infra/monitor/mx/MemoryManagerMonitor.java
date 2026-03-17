package com.kjrepo.infra.monitor.mx;

import java.lang.management.MemoryManagerMXBean;

import com.kjrepo.infra.monitor.mx.bean.MemoryManagerReporterBean;
import com.kjrepo.infra.reporter.utils.Reporter;

public class MemoryManagerMonitor extends AbstractMonitor<MemoryManagerMXBean> {

	@Override
	public void monitor() {
		Reporter.report(new MemoryManagerReporterBean(beans()));
	}

}
