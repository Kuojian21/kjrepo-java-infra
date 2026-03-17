package com.kjrepo.infra.monitor.mx.bean;

import java.lang.management.RuntimeMXBean;
import java.util.List;

public class RuntimeReporterBean extends AbstractReporterBean<RuntimeMXBean> {

	public RuntimeReporterBean(List<RuntimeMXBean> data) {
		super(data);
	}

}
