package com.kjrepo.infra.monitor.mx.bean;

import java.lang.management.MemoryManagerMXBean;
import java.util.List;

public class MemoryManagerReporterBean extends AbstractReporterBean<MemoryManagerMXBean> {

	public MemoryManagerReporterBean(List<MemoryManagerMXBean> data) {
		super(data);
	}

}
