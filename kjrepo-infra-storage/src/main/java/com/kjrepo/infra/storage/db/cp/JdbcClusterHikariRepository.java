package com.kjrepo.infra.storage.db.cp;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.storage.db.jdbc.JdbcCluster;
import com.kjrepo.infra.storage.db.jdbc.JdbcClusterImpl;

public class JdbcClusterHikariRepository<T, K> {

	private final Cluster<NamedParameterJdbcOperations> cluster;

	public JdbcClusterHikariRepository(Cluster<NamedParameterJdbcOperations> cluster) {
		this.cluster = cluster;
	}

	public JdbcCluster<T, K> jdbcCluster(Class<T> clazz) {
		return new JdbcClusterImpl<T, K>(clazz) {
			@Override
			public Cluster<NamedParameterJdbcOperations> cluster() {
				return cluster;
			}

		};
	}

}
