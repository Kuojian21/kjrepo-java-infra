package com.kjrepo.infra.monitor.mx.bean;

import java.util.List;

import com.sun.management.OperatingSystemMXBean;

public class OperatingSystemReporterBean extends AbstractReporterBean<OperatingSystemMXBean> {

	public OperatingSystemReporterBean(List<OperatingSystemMXBean> data) {
		super(data);
	}
}
