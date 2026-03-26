package com.kjrepo.infra.monitor.mxbean.handler;

import java.lang.management.MemoryManagerMXBean;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.kjrepo.infra.monitor.mxbean.holder.MemoryManagerMxbeanHolder;

public class MemoryManagerMxbeanHandler extends AbstractMxbeanHandler<MemoryManagerMXBean, MemoryManagerMxbeanHolder> {

	@Override
	public Map<String, Object> doHandle(MemoryManagerMXBean bean) {
		return ImmutableMap.of("memoryPoolNames", bean.getMemoryPoolNames());
	}

}
