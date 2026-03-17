package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.ThreadMXBean;
import java.util.Map;

import com.annimon.stream.Stream;
import com.google.common.collect.Maps;
import com.kjrepo.infra.monitor.mx.bean.ThreadReporterBean;

public class ThreadIReporter extends AbstractIReporter<ThreadMXBean, ThreadReporterBean> {

	@Override
	public Map<String, Object> doReport(ThreadMXBean bean) {
		Map<String, Object> data = Maps.newLinkedHashMap();
		data.put("currentThreadCpuTime", bean.getCurrentThreadCpuTime());
		data.put("currentThreadUserTime", bean.getCurrentThreadUserTime());
		data.put("daemonThreadCount", bean.getDaemonThreadCount());
		data.put("peakThreadCount", bean.getPeakThreadCount());
		data.put("threadCount", bean.getThreadCount());
		data.put("totalStartedThreadCount", bean.getTotalStartedThreadCount());
		data.put("deadlockedThreads", bean.findDeadlockedThreads());
		data.put("monitorDeadlockedThreads", bean.findMonitorDeadlockedThreads());
		Stream.ofNullable(bean.findDeadlockedThreads()).map(id -> bean.getThreadInfo(id))
				.forEach(info -> logger.info("{}", info.toString()));
		return data;
	}

}
