package com.kjrepo.infra.monitor.mx.bean;

import java.util.List;

import com.sun.management.UnixOperatingSystemMXBean;

public class OperatingSystemReporterUnixBean extends AbstractReporterBean<UnixOperatingSystemMXBean> {

	public OperatingSystemReporterUnixBean(List<UnixOperatingSystemMXBean> data) {
		super(data);
	}
}
