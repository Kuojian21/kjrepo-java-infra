package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.MemoryMXBean;

import com.kjrepo.infra.monitor.mx.wrapper.MemoryUsageWrapper;

public class MemoryIReporter extends AbstractIReporter<MemoryIReporterBean> {

	@Override
	public void report(MemoryIReporterBean data) {
		MemoryMXBean bean = data.data();
		logger.info("memory heap-usage " + MemoryUsageWrapper.of(bean.getHeapMemoryUsage()).toString());
		logger.info("momory noheap-usage " + MemoryUsageWrapper.of(bean.getNonHeapMemoryUsage()).toString());
	}

}
