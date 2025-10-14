package com.kjrepo.infra.monitor.mx;

import java.lang.management.GarbageCollectorMXBean;

import com.kjrepo.infra.monitor.mx.reporter.GarbageCollectorIReporterBean;
import com.kjrepo.infra.reporter.utils.Reporter;

public class GarbageCollectorMonitor extends AbstractMonitor<GarbageCollectorMXBean> {

	@Override
	public void monitor() {
		Reporter.report(new GarbageCollectorIReporterBean(beans()));
	}

}
