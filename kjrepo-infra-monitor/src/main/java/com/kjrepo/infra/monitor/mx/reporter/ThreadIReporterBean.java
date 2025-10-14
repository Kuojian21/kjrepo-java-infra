package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.ThreadMXBean;

public class ThreadIReporterBean extends AbstractIReporterBean<ThreadMXBean> {

	public ThreadIReporterBean(ThreadMXBean data) {
		super(data);
	}

}
