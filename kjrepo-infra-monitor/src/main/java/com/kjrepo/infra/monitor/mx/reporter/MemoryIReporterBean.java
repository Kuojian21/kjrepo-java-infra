package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.MemoryMXBean;

public class MemoryIReporterBean extends AbstractIReporterBean<MemoryMXBean> {

	public MemoryIReporterBean(MemoryMXBean data) {
		super(data);
	}

}
