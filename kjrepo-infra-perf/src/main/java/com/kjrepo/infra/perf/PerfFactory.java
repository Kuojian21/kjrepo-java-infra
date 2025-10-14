package com.kjrepo.infra.perf;

import java.util.ServiceLoader;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class PerfFactory {

	public static final PerfFactory DEFAULT = new PerfFactory(new PerfLogger(Optional
			.of(Lists.newArrayList(ServiceLoader.load(PerfHandler.class)))
			.transform(list -> list.isEmpty() ? null : list).or(() -> Lists.newArrayList(new PerfHandlerConsole()))));

	private final PerfLogger perfLogger;

	public PerfFactory(PerfLogger perfLogger) {
		super();
		this.perfLogger = perfLogger;
	}

	public PerfContext perfContext(PerfLogTag tag) {
		return new PerfContext(this.perfLogger, tag);
	}

}
