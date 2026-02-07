package com.kjrepo.infra.storage.db.jdbc;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

public abstract class KjdbcClusterImpl<T> implements KjdbcCluster<T> {

	private final Class<T> clazz;

	public KjdbcClusterImpl(Class<T> clazz) {
		this.clazz = clazz;
	}

	public Kjdbc<T> sharding(Long key) {
		return new KjdbcImpl<T>(this.clazz) {

			@Override
			public NamedParameterJdbcOperations jdbcTemplate() {
				return cluster().getResource(key);
			}
		};
	}

}
