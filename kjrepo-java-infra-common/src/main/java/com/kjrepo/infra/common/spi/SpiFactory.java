package com.kjrepo.infra.common.spi;

import java.util.List;
import java.util.ServiceLoader;

import com.annimon.stream.Stream;
import com.google.common.collect.Lists;

public class SpiFactory {

	public static <T> Stream<T> stream(Class<T> clazz) {
		return Stream.of(load(clazz));
	}

	public static <T> List<T> list(Class<T> clazz) {
		return Lists.newArrayList(load(clazz));
	}

	public static <T> ServiceLoader<T> load(Class<T> clazz) {
		return ServiceLoader.load(clazz);
	}

}
