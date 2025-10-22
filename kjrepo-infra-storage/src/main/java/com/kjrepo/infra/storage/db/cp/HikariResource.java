package com.kjrepo.infra.storage.db.cp;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.register.resource.IResource;
import com.kjrepo.infra.storage.db.repository.KjdbcRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public interface HikariResource extends IResource<HikariConfig, KjdbcRepository> {

	default Function<HikariConfig, KjdbcRepository> mapper() {
		return conf -> new KjdbcRepository(new HikariDataSource(conf));
	}

	default void close(NamedParameterJdbcOperations resource) {
		((HikariDataSource) ((NamedParameterJdbcTemplate) resource).getJdbcTemplate().getDataSource()).close();
	}

}