package com.kjrepo.infra.reporter.holder;

import java.util.List;

import com.annimon.stream.Optional;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.reporter.IReporter;
import com.kjrepo.infra.reporter.bean.IReporterBean;

@SuppressWarnings("rawtypes")
public class IReporterHolder {

	public static IReporterHolder of(List<IReporter> reporters) {
		return new IReporterHolder(reporters);
	}

	private final List<IReporter> reporters;

	public IReporterHolder(List<IReporter> reporters) {
		this.reporters = Optional.ofNullable(reporters).orElseGet(() -> Lists.newArrayList());
	}

	@SuppressWarnings("unchecked")
	public <D extends IReporterBean> void report(D data) {
		this.reporters.forEach(reporter -> {
			try {
				((IReporter<D>) reporter).report(data);
			} catch (Exception e) {
				LoggerUtils.logger(reporter.getClass()).error("", e);
			}
		});
	}

}
