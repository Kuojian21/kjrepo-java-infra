package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.GarbageCollectorMXBean;
import java.util.List;

public class GarbageCollectorIReporterBean extends AbstractIReporterBean<List<GarbageCollectorMXBean>> {

	public GarbageCollectorIReporterBean(List<GarbageCollectorMXBean> data) {
		super(data);
	}

}
