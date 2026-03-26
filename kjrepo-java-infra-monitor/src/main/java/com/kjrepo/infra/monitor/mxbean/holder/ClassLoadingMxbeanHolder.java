package com.kjrepo.infra.monitor.mxbean.holder;

import java.lang.management.ClassLoadingMXBean;
import java.util.List;

public class ClassLoadingMxbeanHolder extends AbstractMxbeanHolder<ClassLoadingMXBean> {

	public ClassLoadingMxbeanHolder(List<ClassLoadingMXBean> data) {
		super(data);
	}

}
