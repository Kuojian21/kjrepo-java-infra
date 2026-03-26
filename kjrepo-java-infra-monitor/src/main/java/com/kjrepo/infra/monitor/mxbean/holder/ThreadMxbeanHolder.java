package com.kjrepo.infra.monitor.mxbean.holder;

import java.lang.management.ThreadMXBean;
import java.util.List;

public class ThreadMxbeanHolder extends AbstractMxbeanHolder<ThreadMXBean> {

	public ThreadMxbeanHolder(List<ThreadMXBean> data) {
		super(data);
	}

}
