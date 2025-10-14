package com.kjrepo.infra.reporter.utils;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.ServiceLoader;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.reporter.IReporter;
import com.kjrepo.infra.reporter.bean.IReporterBean;
import com.kjrepo.infra.reporter.holder.IReporterHolder;
import com.kjrepo.infra.text.json.utils.TypeMapperUtils;

public class Reporter {

	private static final LazySupplier<Map<Type, IReporterHolder>> repo = LazySupplier.wrap(() -> {
		return Stream.of(Lists.newArrayList(ServiceLoader.load(IReporter.class))).groupBy(
				ir -> Lists.newArrayList(TypeMapperUtils.mapper(ir.getClass()).get(IReporter.class).values()).get(0))
				.collect(Collectors.toMap(Map.Entry::getKey, e -> IReporterHolder.of(e.getValue())));

	});

	public static <D extends IReporterBean> void report(D data) {
		if (data == null) {
			return;
		}
		Optional.ofNullable(repo.get().get(data.getClass())).ifPresent(h -> h.report(data));
	}

}
