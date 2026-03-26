package com.kjrepo.infra.storage.legacy;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.cluster.resource.MasterResource;
import com.kjrepo.infra.storage.db.jdbc.KjdbcHolder;
import com.kjrepo.infra.storage.db.jdbc.KjdbcRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public interface HikariStandbyResource
		extends MasterResource<NamedParameterJdbcOperations, HikariConfig, HikariStandbyInfo> {

	@Override
	default Function<InstanceInfo<HikariConfig>, NamedParameterJdbcOperations> mapper() {
		return info -> new NamedParameterJdbcTemplate(new HikariDataSource(info.getInfo()));
	}

	default void close(NamedParameterJdbcOperations resource) {
		((HikariDataSource) ((NamedParameterJdbcTemplate) resource).getJdbcTemplate().getDataSource()).close();
	}

	default KjdbcRepository getRepository() {
		return new KjdbcRepository() {
			@Override
			public KjdbcHolder holder(boolean master) {
				return KjdbcHolder.of(KjdbcRepositoryFactory.standby(getResource()));
			}

		};
	}

}
