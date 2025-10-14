package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.MemoryPoolMXBean;
import java.util.List;

import com.annimon.stream.Stream;
import com.kjrepo.infra.monitor.mx.wrapper.MemoryUsageWrapper;

public class MemoryPoolIReporter extends AbstractIReporter<MemoryPoolIReporterBean> {

	@Override
	public void report(MemoryPoolIReporterBean data) {
		List<MemoryPoolMXBean> beans = data.data();
		Stream.of(beans).sorted((a, b) -> a.getType().name().compareTo(b.getType().name())).forEach(bean -> {
			logger.info("memory-pool usage type:{} name:{}  {}", bean.getType(), bean.getName(),
					MemoryUsageWrapper.of(bean.getUsage()).toString());
			logger.info("memory-pool collection-usage type:{} name:{}  {}", bean.getType(), bean.getName(),
					MemoryUsageWrapper.of(bean.getCollectionUsage()).toString());
			logger.info("memory-pool peek-usage type:{} name:{} {}", bean.getType(), bean.getName(),
					MemoryUsageWrapper.of(bean.getPeakUsage()).toString());
		});
	}

}
