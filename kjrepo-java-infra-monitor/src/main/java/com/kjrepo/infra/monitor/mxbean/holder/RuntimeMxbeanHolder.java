package com.kjrepo.infra.monitor.mxbean.holder;

import java.lang.management.RuntimeMXBean;
import java.util.List;

public class RuntimeMxbeanHolder extends AbstractMxbeanHolder<RuntimeMXBean> {

	public RuntimeMxbeanHolder(List<RuntimeMXBean> data) {
		super(data);
	}

}
