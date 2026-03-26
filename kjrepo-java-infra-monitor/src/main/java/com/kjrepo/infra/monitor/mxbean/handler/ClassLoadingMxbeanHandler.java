package com.kjrepo.infra.monitor.mxbean.handler;

import java.lang.management.ClassLoadingMXBean;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.kjrepo.infra.monitor.mxbean.holder.ClassLoadingMxbeanHolder;

public class ClassLoadingMxbeanHandler extends AbstractMxbeanHandler<ClassLoadingMXBean, ClassLoadingMxbeanHolder> {

	@Override
	public Map<String, Object> doHandle(ClassLoadingMXBean bean) {
		return ImmutableMap.of("loadedClassCount", bean.getLoadedClassCount(), //
				"totalLoadedClassCount", bean.getTotalLoadedClassCount(), //
				"unloadedClassCount", bean.getUnloadedClassCount() //
		);
	}

}
