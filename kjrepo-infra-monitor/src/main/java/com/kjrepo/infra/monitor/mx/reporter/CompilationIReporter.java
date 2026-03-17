package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.CompilationMXBean;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.monitor.mx.bean.CompilationReportBean;

public class CompilationIReporter extends AbstractIReporter<CompilationMXBean, CompilationReportBean> {

	@Override
	public Map<String, Object> doReport(CompilationMXBean bean) {
		return ImmutableMap.of("totalCompilationTime", N_humanUtils.formatMills(bean.getTotalCompilationTime()));
	}

}
