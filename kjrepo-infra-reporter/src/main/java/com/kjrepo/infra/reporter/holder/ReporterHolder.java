package com.kjrepo.infra.reporter.holder;

import java.util.List;
import java.util.Set;

import com.annimon.stream.Optional;
import com.google.common.collect.Sets;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.reporter.IReporter;
import com.kjrepo.infra.reporter.bean.IReporterBean;

@SuppressWarnings("rawtypes")
public class ReporterHolder {

	public static ReporterHolder of(List<IReporter> reporters) {
		return new ReporterHolder(reporters);
	}

	private final Set<IReporter> reporters = Sets.newConcurrentHashSet();

	public ReporterHolder(List<IReporter> reporters) {
		Optional.ofNullable(reporters).ifPresent(ReporterHolder.this.reporters::addAll);
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

	public <D extends IReporterBean> void register(IReporter<D> reporter) {
		this.reporters.add(reporter);
	}

}
