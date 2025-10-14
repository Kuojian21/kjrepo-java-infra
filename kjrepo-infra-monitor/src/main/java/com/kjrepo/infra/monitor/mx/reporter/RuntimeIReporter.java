package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.RuntimeMXBean;

import com.kjrepo.infra.common.utils.HumanUtils;

public class RuntimeIReporter extends AbstractIReporter<RuntimeIReporterBean> {

	@Override
	public void report(RuntimeIReporterBean data) {
		RuntimeMXBean bean = data.data();
		logger.info("runtime pid:{} name:{} uptime:{}", bean.getPid(), bean.getName(),
				HumanUtils.formatMills(bean.getUptime()));
	}

}
