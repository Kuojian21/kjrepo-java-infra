package com.kjrepo.infra.common.bean;

import java.util.Map;

import com.google.common.collect.Maps;

public class MapBuilder<K, V> extends BeanBuilder<Map<K, V>> {

	public static <K, V> MapBuilder<K, V> builder() {
		return new MapBuilder<K, V>(Maps.newHashMap());

	}

	public MapBuilder(Map<K, V> bean) {
		super(bean);
	}

	public MapBuilder<K, V> put(K key, V val) {
		super.accept(bean -> bean.put(key, val));
		return this;
	}

}
