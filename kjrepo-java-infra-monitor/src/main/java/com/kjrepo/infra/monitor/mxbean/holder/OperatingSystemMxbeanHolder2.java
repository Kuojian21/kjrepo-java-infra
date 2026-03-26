package com.kjrepo.infra.monitor.mxbean.holder;

import java.util.List;

import com.sun.management.OperatingSystemMXBean;

public class OperatingSystemMxbeanHolder2 extends AbstractMxbeanHolder<OperatingSystemMXBean> {

	public OperatingSystemMxbeanHolder2(List<OperatingSystemMXBean> data) {
		super(data);
	}
}
