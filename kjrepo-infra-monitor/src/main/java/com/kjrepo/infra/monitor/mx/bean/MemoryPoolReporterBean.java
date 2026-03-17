package com.kjrepo.infra.monitor.mx.bean;

import java.lang.management.MemoryPoolMXBean;
import java.util.List;

public class MemoryPoolReporterBean extends AbstractReporterBean<MemoryPoolMXBean> {

	public MemoryPoolReporterBean(List<MemoryPoolMXBean> data) {
		super(data);
	}

}
