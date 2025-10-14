package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.RuntimeMXBean;

public class RuntimeIReporterBean extends AbstractIReporterBean<RuntimeMXBean> {

	public RuntimeIReporterBean(RuntimeMXBean data) {
		super(data);
	}

}
