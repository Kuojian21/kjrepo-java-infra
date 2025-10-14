package com.kjrepo.infra.storage.db.jdbc;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

public abstract class JdbcClusterImpl<T, K> implements JdbcCluster<T, K> {

	private final Class<T> clazz;

	public JdbcClusterImpl(Class<T> clazz) {
		this.clazz = clazz;
	}

	public Jdbc<T> sharding(K key) {
		return new JdbcImpl<T>(this.clazz) {

			@Override
			public NamedParameterJdbcOperations jdbcTemplate() {
				return cluster().getResource(key);
			}

		};
	}

}
