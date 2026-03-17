package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.MemoryManagerMXBean;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.kjrepo.infra.monitor.mx.bean.MemoryManagerReporterBean;

public class MemoryManagerIReporter extends AbstractIReporter<MemoryManagerMXBean, MemoryManagerReporterBean> {

	@Override
	public Map<String, Object> doReport(MemoryManagerMXBean bean) {
		return ImmutableMap.of("memoryPoolNames", bean.getMemoryPoolNames());
	}

}
