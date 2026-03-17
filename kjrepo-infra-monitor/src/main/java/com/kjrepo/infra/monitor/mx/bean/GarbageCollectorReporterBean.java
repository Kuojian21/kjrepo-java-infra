package com.kjrepo.infra.monitor.mx.bean;

import java.lang.management.GarbageCollectorMXBean;
import java.util.List;

public class GarbageCollectorReporterBean extends AbstractReporterBean<GarbageCollectorMXBean> {

	public GarbageCollectorReporterBean(List<GarbageCollectorMXBean> data) {
		super(data);
	}

}
