package com.kjrepo.infra.monitor.mxbean.holder;

import java.lang.management.MemoryPoolMXBean;
import java.util.List;

public class MemoryPoolMxbeanHolder extends AbstractMxbeanHolder<MemoryPoolMXBean> {

	public MemoryPoolMxbeanHolder(List<MemoryPoolMXBean> data) {
		super(data);
	}

}
