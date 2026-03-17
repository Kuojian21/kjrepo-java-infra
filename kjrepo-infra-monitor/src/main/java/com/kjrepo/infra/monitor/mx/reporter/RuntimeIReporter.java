package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.RuntimeMXBean;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.google.common.collect.Maps;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.monitor.mx.bean.RuntimeReporterBean;

public class RuntimeIReporter extends AbstractIReporter<RuntimeMXBean, RuntimeReporterBean> {

	@Override
	public Map<String, Object> doReport(RuntimeMXBean bean) {
		Map<String, Object> data = Maps.newLinkedHashMap();
		data.put("pid", bean.getPid());
		data.put("uptime", N_humanUtils.formatMills(bean.getUptime()));
//		data.put("bootClassPath", bean.getBootClassPath());
//		data.put("classPath", bean.getClassPath());
//		data.put("libraryPath", bean.getLibraryPath());
		data.put("managementSpecVersion", bean.getManagementSpecVersion());
		data.put("specName", bean.getSpecName());
		data.put("specVendor", bean.getSpecVendor());
		data.put("specVersion", bean.getSpecVersion());
		data.put("vmName", bean.getVmName());
		data.put("vmVendor", bean.getVmVendor());
		data.put("vmVersion", bean.getVmVersion());
		data.put("inputArguments", bean.getInputArguments());
		data.put("startTime", DateFormatUtils.format(bean.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
//		data.put("systemProperties", bean.getSystemProperties());
		return data;
	}

}
