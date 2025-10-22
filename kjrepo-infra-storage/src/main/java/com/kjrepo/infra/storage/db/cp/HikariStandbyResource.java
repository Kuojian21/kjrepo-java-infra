package com.kjrepo.infra.storage.db.cp;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.cluster.resource.StandbyResource;
import com.kjrepo.infra.storage.db.repository.KjdbcRepositoryFactory;
import com.kjrepo.infra.storage.db.repository.KjdbcRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public interface HikariStandbyResource
		extends StandbyResource<NamedParameterJdbcOperations, HikariConfig, HikariStandbyInfo> {

	@Override
	default Function<HikariConfig, NamedParameterJdbcOperations> mapper() {
		return info -> new NamedParameterJdbcTemplate(new HikariDataSource(info));
	}

	default void close(NamedParameterJdbcOperations resource) {
		((HikariDataSource) ((NamedParameterJdbcTemplate) resource).getJdbcTemplate().getDataSource()).close();
	}

	default KjdbcRepository getRepository() {
		return new KjdbcRepository(KjdbcRepositoryFactory.standby(getResource()));
	}

}
