package com.kjrepo.infra.storage.db.jdbc.hikari;

import javax.sql.DataSource;

import com.kjrepo.infra.storage.db.jdbc.KjdbcResource;
import com.kjrepo.infra.storage.db.utils.DB_tag_utils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public interface HikariBaseResource extends KjdbcResource<HikariConfig> {

	@Override
	default DataSource dataSource(HikariConfig info) {
		return new HikariDataSource(info);
	}

	@Override
	default String tag(HikariConfig info) {
		return DB_tag_utils.tag(info.getJdbcUrl());
	}

}
