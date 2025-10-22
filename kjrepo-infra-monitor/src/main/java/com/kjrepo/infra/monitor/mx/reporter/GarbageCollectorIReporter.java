package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.GarbageCollectorMXBean;
import java.util.List;

import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.number.N_humanUtils;

public class GarbageCollectorIReporter extends AbstractIReporter<GarbageCollectorIReporterBean> {

	private final Logger logger = LoggerUtils.logger(getClass());

	@Override
	public void report(GarbageCollectorIReporterBean data) {
		List<GarbageCollectorMXBean> beans = data.data();
		beans.forEach(bean -> {
			logger.info("garbage-collector name:{} count:{} time:{}", bean.getName(), bean.getCollectionCount(),
					N_humanUtils.formatMills(bean.getCollectionTime()));
		});
	}

}
