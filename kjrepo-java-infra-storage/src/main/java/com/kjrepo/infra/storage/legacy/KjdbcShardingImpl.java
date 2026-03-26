package com.kjrepo.infra.storage.legacy;

import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.annimon.stream.function.Function;
import com.google.common.collect.Maps;
import com.kjrepo.infra.storage.db.jdbc.Kjdbc;
import com.kjrepo.infra.storage.db.jdbc.KjdbcHolder;
import com.kjrepo.infra.storage.db.jdbc.KjdbcImpl;

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
			public KjdbcHolder holder(boolean master) {
				return KjdbcHolder.of(KjdbcShardingImpl.this.jdbcTemplate());
			}
		});
	}

	public abstract NamedParameterJdbcTemplate jdbcTemplate();
}
