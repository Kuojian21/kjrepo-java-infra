package com.kjrepo.infra.monitor.mx.bean;

import java.lang.management.ThreadMXBean;
import java.util.List;

public class ThreadReporterBean extends AbstractReporterBean<ThreadMXBean> {

	public ThreadReporterBean(List<ThreadMXBean> data) {
		super(data);
	}

}
