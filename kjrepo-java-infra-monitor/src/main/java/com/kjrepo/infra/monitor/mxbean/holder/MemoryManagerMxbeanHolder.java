package com.kjrepo.infra.monitor.mxbean.holder;

import java.lang.management.MemoryManagerMXBean;
import java.util.List;

public class MemoryManagerMxbeanHolder extends AbstractMxbeanHolder<MemoryManagerMXBean> {

	public MemoryManagerMxbeanHolder(List<MemoryManagerMXBean> data) {
		super(data);
	}

}
