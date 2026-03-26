package com.kjrepo.infra.monitor.mxbean.holder;

import java.lang.management.GarbageCollectorMXBean;
import java.util.List;

public class GarbageCollectorMxbeanHolder extends AbstractMxbeanHolder<GarbageCollectorMXBean> {

	public GarbageCollectorMxbeanHolder(List<GarbageCollectorMXBean> data) {
		super(data);
	}

}
