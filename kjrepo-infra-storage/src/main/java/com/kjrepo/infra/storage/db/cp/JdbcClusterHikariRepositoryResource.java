package com.kjrepo.infra.storage.db.cp;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.cluster.instance.InstanceInfo;
import com.kjrepo.infra.cluster.resource.ClusterResource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public interface JdbcClusterHikariRepositoryResource
		extends ClusterResource<NamedParameterJdbcOperations, HikariConfig> {

	@Override
	default Function<InstanceInfo<HikariConfig>, NamedParameterJdbcOperations> mapper() {
		return info -> (NamedParameterJdbcOperations) new NamedParameterJdbcTemplate(
				new HikariDataSource((HikariConfig) info.getInfo()));
	}

	default void close(NamedParameterJdbcOperations resource) {
		((HikariDataSource) ((NamedParameterJdbcTemplate) resource).getJdbcTemplate().getDataSource()).close();
	}

	default <T, K> JdbcClusterHikariRepository<T, K> getRepository() {
		return new JdbcClusterHikariRepository<>(this.getResource());
	}
}
