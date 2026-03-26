package com.kjrepo.infra.monitor.mxbean.holder;

import java.util.List;

import java.lang.management.OperatingSystemMXBean;

public class OperatingSystemMxbeanHolder extends AbstractMxbeanHolder<OperatingSystemMXBean> {

	public OperatingSystemMxbeanHolder(List<OperatingSystemMXBean> data) {
		super(data);
	}
}
