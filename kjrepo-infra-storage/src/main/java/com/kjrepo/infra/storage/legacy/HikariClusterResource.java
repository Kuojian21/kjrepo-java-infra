package com.kjrepo.infra.storage.legacy;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.cluster.resource.ClusterResource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public interface HikariClusterResource
		extends ClusterResource<NamedParameterJdbcTemplate, HikariConfig, HikariClusterInfo> {

	@Override
	default Function<InstanceInfo<HikariConfig>, NamedParameterJdbcTemplate> mapper() {
		return info -> new NamedParameterJdbcTemplate(new HikariDataSource(info.getInfo()));
	}

	default void close(NamedParameterJdbcTemplate resource) {
		((HikariDataSource) resource.getJdbcTemplate().getDataSource()).close();
	}

	default <T, K> KjdbcClusterRepository<T, K> getRepository() {
		return new KjdbcClusterRepository<>(this.getResource());
	}
}
