package com.kjrepo.infra.monitor.mxbean.handler;

import java.lang.management.GarbageCollectorMXBean;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.kjrepo.infra.monitor.mxbean.holder.GarbageCollectorMxbeanHolder;

public class GarbageCollectorMxbeanHandler
		extends AbstractMxbeanHandler<GarbageCollectorMXBean, GarbageCollectorMxbeanHolder> {

	@Override
	public Map<String, Object> doHandle(GarbageCollectorMXBean bean) {
		return ImmutableMap.of("memoryPoolNames", bean.getMemoryPoolNames(), //
				"collectionCount", bean.getCollectionCount(), //
				"collectionTime", bean.getCollectionTime() //
		);
	}
}
