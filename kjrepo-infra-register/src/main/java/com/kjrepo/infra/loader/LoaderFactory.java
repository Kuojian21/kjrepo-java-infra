package com.kjrepo.infra.loader;

import java.util.Map;
import java.util.ServiceLoader;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.info.Pair;

public class LoaderFactory<T extends Loader> {

	private final Map<String, T> contexts;

	public LoaderFactory(Class<T> clazz) {
		try {
			contexts = Stream.of(ServiceLoader.load(clazz)).flatMap(cxt -> Stream.of(Lists.newArrayList(cxt.pkg(), //
					cxt.getClass().getName().replaceAll("\\." + cxt.getClass().getSimpleName() + "$", ""),
					cxt.getClass().getName().replaceAll("\\.register\\." + cxt.getClass().getSimpleName() + "$", ""),
					cxt.getClass().getName()
							.replaceAll("\\.common\\.register\\." + cxt.getClass().getSimpleName() + "$", "")))
					.filter(pkg -> StringUtils.isNotEmpty(pkg)).distinct().map(pkg -> Pair.pair(pkg, cxt)))
					.collect(Collectors.toMap(Pair::getKey, Pair::getValue));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public T getContext(Class<?> clazz) {
		return getContext(clazz.getName());
	}

	public T getContext(String name) {
		T context = contexts.get(name);
		if (context == null) {
			int index = Math.max(name.lastIndexOf("."), name.lastIndexOf("$"));
			if (index > 0) {
				context = getContext(name.substring(0, index));
			}
		}
		return context;
	}

}
