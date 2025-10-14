package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.MemoryPoolMXBean;
import java.util.List;

public class MemoryPoolIReporterBean extends AbstractIReporterBean<List<MemoryPoolMXBean>> {

	public MemoryPoolIReporterBean(List<MemoryPoolMXBean> data) {
		super(data);
	}

}
