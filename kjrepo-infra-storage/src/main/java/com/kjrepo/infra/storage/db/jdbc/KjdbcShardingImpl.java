package com.kjrepo.infra.storage.db.jdbc;

import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.annimon.stream.function.Function;
import com.google.common.collect.Maps;

public abstract class KjdbcShardingImpl<T, K> {

	private final Map<String, Kjdbc<T>> jdbcMap = Maps.newConcurrentMap();
	private final Class<T> clazz;
	private final Function<K, String> sharding;

	public KjdbcShardingImpl(Class<T> clazz, Function<K, String> sharding) {
		this.clazz = clazz;
		this.sharding = sharding;
	}

	public Kjdbc<T> sharding(K key) {
		return jdbcMap.computeIfAbsent(this.sharding.apply(key), suffix -> new KjdbcImpl<>(clazz, suffix) {
			@Override
			public NamedParameterJdbcOperations jdbcTemplate() {
				return KjdbcShardingImpl.this.jdbcTemplate();
			}
		});
	}

	public abstract NamedParameterJdbcOperations jdbcTemplate();
}
