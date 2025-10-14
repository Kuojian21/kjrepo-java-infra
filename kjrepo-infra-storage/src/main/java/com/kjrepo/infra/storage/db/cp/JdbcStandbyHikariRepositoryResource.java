package com.kjrepo.infra.storage.db.cp;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.cluster.resource.StandbyResource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public interface JdbcStandbyHikariRepositoryResource
		extends StandbyResource<NamedParameterJdbcOperations, HikariConfig, HikariStandbyInfo> {

	@Override
	default Function<HikariConfig, NamedParameterJdbcOperations> mapper() {
		return info -> new NamedParameterJdbcTemplate(new HikariDataSource((HikariConfig) info));
	}

	default void close(NamedParameterJdbcOperations resource) {
		((HikariDataSource) ((NamedParameterJdbcTemplate) resource).getJdbcTemplate().getDataSource()).close();
	}

	default JdbcHikariRepository getRepository() {
		return new JdbcHikariRepository(HikariRepositoryFactory.standby(getResource()));
	}

}
