package com.kjrepo.infra.common.spi;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.info.Pair;

public class PkgSpiFactory<T extends PkgSpi> {

	public static <T extends PkgSpi> PkgSpiFactory<T> of(Class<T> clazz) {
		return new PkgSpiFactory<>(clazz);
	}

	private final Map<String, List<T>> spis;

	private PkgSpiFactory(Class<T> clazz) {
		try {
			spis = Stream.of(ServiceLoader.load(clazz))
					.flatMap(spi -> Stream.of(spi.pkgs()).filter(StringUtils::isNotEmpty).map(n -> Pair.pair(n, spi)))
					.groupBy(Pair::getKey).collect(Collectors.toMap(Map.Entry::getKey,
							e -> Stream.of(e.getValue()).map(Pair::getValue).toList()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public T get(Class<?> clazz) {
		return get(clazz.getName());
	}

	public T get(String pkg) {
		return getList(pkg).get(0);
	}

	public List<T> getList(Class<?> clazz) {
		return getList(clazz.getName());
	}

	public List<T> getList(String pkg) {
		List<T> spi = spis.get(pkg);
		if (spi == null) {
			if ("".equals(pkg)) {
				spi = Lists.newArrayList();
			} else {
				spi = getList(pkg.substring(0, Math.max(0, Math.max(pkg.lastIndexOf("."), pkg.lastIndexOf("$")))));
			}
		}
		return spi;
	}

}
