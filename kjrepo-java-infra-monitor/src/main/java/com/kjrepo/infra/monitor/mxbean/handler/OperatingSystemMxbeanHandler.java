package com.kjrepo.infra.monitor.mxbean.handler;

import java.lang.management.OperatingSystemMXBean;

import java.util.Map;
import com.google.common.collect.Maps;
import com.kjrepo.infra.monitor.mxbean.holder.OperatingSystemMxbeanHolder;

public class OperatingSystemMxbeanHandler
		extends AbstractMxbeanHandler<OperatingSystemMXBean, OperatingSystemMxbeanHolder> {

	@Override
	public Map<String, Object> doHandle(OperatingSystemMXBean bean) {
		Map<String, Object> data = Maps.newLinkedHashMap();
		data.put("name", bean.getName());
		data.put("arch", bean.getArch());
		data.put("availableProcessors", bean.getAvailableProcessors());
		data.put("systemLoadAverage", bean.getSystemLoadAverage());
		return data;
	}

}
