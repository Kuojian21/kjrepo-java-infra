package com.kjrepo.infra.monitor.mx.bean;

import java.lang.management.MemoryMXBean;
import java.util.List;

public class MemoryReporterBean extends AbstractReporterBean<MemoryMXBean> {

	public MemoryReporterBean(List<MemoryMXBean> data) {
		super(data);
	}

}
