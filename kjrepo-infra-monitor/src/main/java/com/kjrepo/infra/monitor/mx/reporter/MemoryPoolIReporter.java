package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.MemoryPoolMXBean;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.kjrepo.infra.monitor.mx.bean.MemoryPoolReporterBean;
import com.kjrepo.infra.monitor.mx.utils.MxUtils;

public class MemoryPoolIReporter extends AbstractIReporter<MemoryPoolMXBean, MemoryPoolReporterBean> {

	@Override
	public Map<String, Object> doReport(MemoryPoolMXBean bean) {
		return ImmutableMap.of("type", bean.getType(), //
				"memoryManagerNames", bean.getMemoryManagerNames(), //
				"usage", MxUtils.toMap(bean.getUsage()), //
				"collectionUsage", MxUtils.toMap(bean.getCollectionUsage()), //
				"peakUsage", MxUtils.toMap(bean.getPeakUsage()) //
		);
	}

}
