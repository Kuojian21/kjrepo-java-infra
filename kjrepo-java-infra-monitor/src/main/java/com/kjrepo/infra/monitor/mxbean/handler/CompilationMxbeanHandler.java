package com.kjrepo.infra.monitor.mxbean.handler;

import java.lang.management.CompilationMXBean;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.monitor.mxbean.holder.CompilationMxbeanHolder;

public class CompilationMxbeanHandler extends AbstractMxbeanHandler<CompilationMXBean, CompilationMxbeanHolder> {

	@Override
	public Map<String, Object> doHandle(CompilationMXBean bean) {
		return ImmutableMap.of("totalCompilationTime", N_humanUtils.formatMills(bean.getTotalCompilationTime()));
	}

}
