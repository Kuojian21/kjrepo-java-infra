package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.MemoryMXBean;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.kjrepo.infra.monitor.mx.bean.MemoryReporterBean;
import com.kjrepo.infra.monitor.mx.utils.MxUtils;

public class MemoryIReporter extends AbstractIReporter<MemoryMXBean, MemoryReporterBean> {

	@Override
	public Map<String, Object> doReport(MemoryMXBean bean) {
		return ImmutableMap.of("objectPendingFinalizationCount", bean.getObjectPendingFinalizationCount(), //
				"heapMemoryUsage", MxUtils.toMap(bean.getHeapMemoryUsage()), //
				"nonHeapMemoryUsage", MxUtils.toMap(bean.getNonHeapMemoryUsage()) //
		);
	}

}
