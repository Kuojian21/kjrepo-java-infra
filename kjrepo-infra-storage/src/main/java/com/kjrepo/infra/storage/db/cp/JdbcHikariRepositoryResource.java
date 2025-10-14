package com.kjrepo.infra.storage.db.cp;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.register.resource.IResource;
import com.zaxxer.hikari.HikariConfig;

public interface JdbcHikariRepositoryResource extends IResource<HikariConfig, JdbcHikariRepository> {

	default Function<HikariConfig, JdbcHikariRepository> mapper() {
		return conf -> new JdbcHikariRepository(conf);
	}

}
