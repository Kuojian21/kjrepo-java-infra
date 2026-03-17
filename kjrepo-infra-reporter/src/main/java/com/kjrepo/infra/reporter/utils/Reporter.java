package com.kjrepo.infra.reporter.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.reporter.IReporter;
import com.kjrepo.infra.reporter.bean.IReporterBean;
import com.kjrepo.infra.reporter.holder.ReporterHolder;
import com.kjrepo.infra.text.json.utils.TypeMapperUtils;

public class Reporter {

	private static final Logger logger = LoggerUtils.logger(Reporter.class);
	private static final LazySupplier<Map<Type, ReporterHolder>> repo = LazySupplier.wrap(() -> {
		return Stream.of(Lists.newArrayList(ServiceLoader.load(IReporter.class))).groupBy(
				ir -> Lists.newArrayList(TypeMapperUtils.mapper(ir.getClass()).get(IReporter.class).values()).get(0))
				.collect(Collectors.toMap(Map.Entry::getKey, e -> ReporterHolder.of(e.getValue()),
						Maps::newConcurrentMap));

	});

	public static <D extends IReporterBean> void report(D data) {
		if (data == null) {
			return;
		}
		Optional.ofNullable(repo.get().get(data.getClass())).ifPresent(h -> h.report(data));
	}

	public static void register(Class<?> clazz) {
		try {
			register((IReporter<?>) clazz.getConstructor(new Class<?>[] {}).newInstance(new Object[] {}));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			logger.error("", e);
		}
	}

	public static void register(IReporter<?> reporter) {
		Type clazz = Lists.newArrayList(TypeMapperUtils.mapper(reporter.getClass()).get(IReporter.class).values())
				.get(0);
		repo.get().computeIfAbsent(clazz, k -> ReporterHolder.of(Lists.newArrayList())).register(reporter);
	}

}
