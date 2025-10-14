package com.kjrepo.infra.storage.db.utils;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class JdbcUtils {

	public static HikariConfig defaultHikariConfig() {
		HikariConfig config = new HikariConfig();
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setAutoCommit(true);
		config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(20L));
		config.setValidationTimeout(TimeUnit.SECONDS.toMillis(5L));
		config.setIdleTimeout(TimeUnit.MINUTES.toMillis(1L));
		config.setMaximumPoolSize(100);
		config.setMinimumIdle(0);
		config.setMaxLifetime(0L);
		config.setRegisterMbeans(false);
		return config;
	}

	public static NamedParameterJdbcTemplate paramJdbcTemplate(HikariConfig hikariConfig) {
		return new NamedParameterJdbcTemplate(new HikariDataSource(hikariConfig));
	}

	public static JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

}
