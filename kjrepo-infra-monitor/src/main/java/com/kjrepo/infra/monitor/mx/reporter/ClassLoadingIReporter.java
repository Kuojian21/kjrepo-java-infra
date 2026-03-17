package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.ClassLoadingMXBean;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.kjrepo.infra.monitor.mx.bean.ClassLoadingReportBean;

public class ClassLoadingIReporter extends AbstractIReporter<ClassLoadingMXBean, ClassLoadingReportBean> {

	@Override
	public Map<String, Object> doReport(ClassLoadingMXBean bean) {
		return ImmutableMap.of("loadedClassCount", bean.getLoadedClassCount(), //
				"totalLoadedClassCount", bean.getTotalLoadedClassCount(), //
				"unloadedClassCount", bean.getUnloadedClassCount() //
		);
	}

}
