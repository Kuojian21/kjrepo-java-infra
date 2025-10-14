package com.kjrepo.infra.storage.db.jdbc;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.kjrepo.infra.cluster.Cluster;

public interface JdbcCluster<T, K> {

	Jdbc<T> sharding(K key);

	Cluster<NamedParameterJdbcOperations> cluster();

}
