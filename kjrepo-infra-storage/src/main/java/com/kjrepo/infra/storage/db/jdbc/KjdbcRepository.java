package com.kjrepo.infra.storage.db.jdbc;

import java.util.Map;

import com.google.common.collect.Maps;

public abstract class KjdbcRepository {

	private final Map<String, Kjdbc<?>> jdbcs = Maps.newConcurrentMap();

	public final <T> Kjdbc<T> jdbc(Class<T> clazz) {
		return jdbc(clazz, "");
	}

	@SuppressWarnings("unchecked")
	public final <T> Kjdbc<T> jdbc(Class<T> clazz, String suffix) {
		return (Kjdbc<T>) this.jdbcs.computeIfAbsent(clazz.getName() + "#" + suffix,
				k -> new KjdbcImpl<T>(clazz, suffix) {
					@Override
					public KjdbcHolder holder(boolean master) {
						return KjdbcRepository.this.holder(master);
					}
				});
	}

	public abstract KjdbcHolder holder(boolean master);

}
