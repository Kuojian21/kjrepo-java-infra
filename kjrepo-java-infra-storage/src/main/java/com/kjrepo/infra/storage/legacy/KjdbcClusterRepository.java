package com.kjrepo.infra.storage.legacy;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.kjrepo.infra.cluster.Cluster;

public class KjdbcClusterRepository<T, K> {

	private final Cluster<NamedParameterJdbcTemplate> cluster;

	public KjdbcClusterRepository(Cluster<NamedParameterJdbcTemplate> cluster) {
		this.cluster = cluster;
	}

	public KjdbcCluster<T> cluster(Class<T> clazz) {
		return new KjdbcClusterImpl<T>(clazz) {
			@Override
			public Cluster<NamedParameterJdbcTemplate> cluster() {
				return cluster;
			}
		};
	}

}
