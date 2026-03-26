package com.kjrepo.infra.common.spi;

import java.util.List;
import java.util.Map;

import com.annimon.stream.Collectors;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.utils.TypeMapperUtils;

public class ParamSpiFactory<T extends ParamSpi<? extends ParamSpiBean>> {

	public static <T extends ParamSpi<?>> ParamSpiFactory<T> of(Class<T> clazz) {
		return new ParamSpiFactory<>(clazz);
	}

	private final Map<Class<?>, List<T>> spis;

	public ParamSpiFactory(Class<T> clazz) {
		spis = SpiFactory.stream(clazz).groupBy(
				ir -> (Class<?>) Lists.newArrayList(TypeMapperUtils.mapper(ir.getClass()).get(clazz).values()).get(0))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Maps::newConcurrentMap));
	}

	@SuppressWarnings("unchecked")
	public List<T> getList(Class<? extends ParamSpiBean> clazz) {
		List<T> spi = spis.get(clazz);
		if (spi == null) {
			if (!ParamSpiBean.class.isAssignableFrom(clazz.getSuperclass())) {
				spi = Lists.newArrayList();
			} else {
				spi = getList((Class<? extends ParamSpiBean>) clazz.getSuperclass());
			}
		}
		return spi;
	}

}
