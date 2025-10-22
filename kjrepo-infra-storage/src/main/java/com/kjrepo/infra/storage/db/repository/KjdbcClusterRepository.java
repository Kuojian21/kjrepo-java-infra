package com.kjrepo.infra.storage.db.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.storage.db.jdbc.KjdbcCluster;
import com.kjrepo.infra.storage.db.jdbc.KjdbcClusterImpl;

public class KjdbcClusterRepository<T, K> {

	private final Cluster<NamedParameterJdbcOperations> cluster;

	public KjdbcClusterRepository(Cluster<NamedParameterJdbcOperations> cluster) {
		this.cluster = cluster;
	}

	public KjdbcCluster<T, K> cluster(Class<T> clazz) {
		return new KjdbcClusterImpl<T, K>(clazz) {
			@Override
			public Cluster<NamedParameterJdbcOperations> cluster() {
				return cluster;
			}
		};
	}

}
