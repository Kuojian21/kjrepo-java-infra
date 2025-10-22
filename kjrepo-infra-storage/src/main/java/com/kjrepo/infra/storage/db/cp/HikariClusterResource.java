package com.kjrepo.infra.storage.db.cp;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.cluster.resource.ClusterResource;
import com.kjrepo.infra.storage.db.repository.KjdbcClusterRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public interface HikariClusterResource
		extends ClusterResource<NamedParameterJdbcOperations, HikariConfig, HikariClusterInfo> {

	@Override
	default Function<HikariConfig, NamedParameterJdbcOperations> mapper() {
		return info -> new NamedParameterJdbcTemplate(new HikariDataSource(info));
	}

	default void close(NamedParameterJdbcOperations resource) {
		((HikariDataSource) ((NamedParameterJdbcTemplate) resource).getJdbcTemplate().getDataSource()).close();
	}

	default <T, K> KjdbcClusterRepository<T, K> getRepository() {
		return new KjdbcClusterRepository<>(this.getResource());
	}
}
