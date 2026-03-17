package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.GarbageCollectorMXBean;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.kjrepo.infra.monitor.mx.bean.GarbageCollectorReporterBean;

public class GarbageCollectorIReporter extends AbstractIReporter<GarbageCollectorMXBean, GarbageCollectorReporterBean> {

	@Override
	public Map<String, Object> doReport(GarbageCollectorMXBean bean) {
		return ImmutableMap.of("memoryPoolNames", bean.getMemoryPoolNames(), //
				"collectionCount", bean.getCollectionCount(), //
				"collectionTime", bean.getCollectionTime() //
		);
	}
}
