package com.kjrepo.infra.monitor.mxbean.holder;

import java.lang.management.CompilationMXBean;
import java.util.List;

public class CompilationMxbeanHolder extends AbstractMxbeanHolder<CompilationMXBean> {

	public CompilationMxbeanHolder(List<CompilationMXBean> data) {
		super(data);
	}

}
