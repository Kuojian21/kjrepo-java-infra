package com.kjrepo.infra.monitor.mx.reporter;

import java.lang.management.PlatformManagedObject;
import java.util.Map;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kjrepo.infra.monitor.mx.bean.AbstractReporterBean;
import com.kjrepo.infra.reporter.IReporter;
import com.kjrepo.infra.text.json.JsonUtils;

public abstract class AbstractIReporter<D extends PlatformManagedObject, T extends AbstractReporterBean<D>>
		implements IReporter<T> {

	@Override
	public final void report(T data) {
		logger.info("{}", JsonUtils.toPrettyJson(Stream.ofNullable(data.beans())
				.collect(Collectors.toMap(bean -> bean.getObjectName().toString(), bean -> doReport(bean)))));
	}

	public abstract Map<String, Object> doReport(D bean);

}
