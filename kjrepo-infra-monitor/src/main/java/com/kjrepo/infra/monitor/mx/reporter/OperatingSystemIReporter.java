package com.kjrepo.infra.monitor.mx.reporter;

import com.sun.management.OperatingSystemMXBean;

import java.util.Map;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.monitor.mx.bean.OperatingSystemReporterBean;

public class OperatingSystemIReporter extends AbstractIReporter<OperatingSystemMXBean, OperatingSystemReporterBean> {

	@Override
	public Map<String, Object> doReport(OperatingSystemMXBean bean) {
		Map<String, Object> data = Maps.newLinkedHashMap();
		data.put("name", bean.getName());
		data.put("arch", bean.getArch());
		data.put("availableProcessors", bean.getAvailableProcessors());
		data.put("systemLoadAverage", bean.getSystemLoadAverage());
		data.put("committedVirtualMemorySize", N_humanUtils.formatByte(bean.getCommittedVirtualMemorySize()));
		data.put("totalMemorySize", N_humanUtils.formatByte(bean.getTotalMemorySize()));
		data.put("freeMemorySize", N_humanUtils.formatByte(bean.getFreeMemorySize()));
		data.put("totalSwapSpaceSize", N_humanUtils.formatByte(bean.getTotalSwapSpaceSize()));
		data.put("freeSwapSpaceSize", N_humanUtils.formatByte(bean.getFreeSwapSpaceSize()));

		data.put("cpuLoad", bean.getCpuLoad());
		data.put("processCpuLoad", bean.getProcessCpuLoad());
		data.put("processCpuTime", N_humanUtils.formatNanos(bean.getProcessCpuTime()));
		return data;
	}

}
