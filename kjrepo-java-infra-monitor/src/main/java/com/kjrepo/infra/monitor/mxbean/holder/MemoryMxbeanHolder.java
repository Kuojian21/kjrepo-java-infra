package com.kjrepo.infra.monitor.mxbean.holder;

import java.lang.management.MemoryMXBean;
import java.util.List;

public class MemoryMxbeanHolder extends AbstractMxbeanHolder<MemoryMXBean> {

	public MemoryMxbeanHolder(List<MemoryMXBean> data) {
		super(data);
	}

}
