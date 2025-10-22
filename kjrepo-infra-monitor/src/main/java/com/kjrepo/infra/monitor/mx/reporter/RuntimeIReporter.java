package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.RuntimeMXBean;

import com.kjrepo.infra.common.number.N_humanUtils;

public class RuntimeIReporter extends AbstractIReporter<RuntimeIReporterBean> {

	@Override
	public void report(RuntimeIReporterBean data) {
		RuntimeMXBean bean = data.data();
		logger.info("runtime pid:{} name:{} uptime:{}", bean.getPid(), bean.getName(),
				N_humanUtils.formatMills(bean.getUptime()));
	}

}
