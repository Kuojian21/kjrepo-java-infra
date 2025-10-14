package com.kjrepo.infra.storage.db.jdbc;

import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.annimon.stream.function.Function;
import com.google.common.collect.Maps;
import com.kjrepo.infra.cluster.Cluster;

public abstract class JdbcShardingClusterImpl<T, K> {

	private final Map<String, Jdbc<T>> jdbcMap = Maps.newConcurrentMap();
	private final Class<T> clazz;
	private final Function<K, String> sharding;

	public JdbcShardingClusterImpl(Class<T> clazz, Function<K, String> sharding) {
		this.clazz = clazz;
		this.sharding = sharding;
	}

	public Jdbc<T> sharding(K key) {
		return jdbcMap.computeIfAbsent(this.sharding.apply(key), suffix -> new JdbcImpl<>(clazz, suffix) {
			@Override
			public NamedParameterJdbcOperations jdbcTemplate() {
				return cluster().getResource(key);
			}
		});
	}

	protected abstract Cluster<NamedParameterJdbcOperations> cluster();

}
