package com.kjrepo.infra.monitor.mxbean.holder;

import java.util.List;

import com.sun.management.UnixOperatingSystemMXBean;

public class OperatingSystemUnixMxbeanHolder extends AbstractMxbeanHolder<UnixOperatingSystemMXBean> {

	public OperatingSystemUnixMxbeanHolder(List<UnixOperatingSystemMXBean> data) {
		super(data);
	}
}
