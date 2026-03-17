package com.kjrepo.infra.storage.legacy;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.storage.db.jdbc.Kjdbc;

public interface KjdbcCluster<T> {

	Kjdbc<T> sharding(Long key);

	Cluster<NamedParameterJdbcTemplate> cluster();

}
