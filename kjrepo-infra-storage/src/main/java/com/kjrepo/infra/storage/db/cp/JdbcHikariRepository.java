package com.kjrepo.infra.storage.db.cp;

import java.io.Closeable;
import java.io.IOException;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.kjrepo.infra.storage.db.jdbc.JdbcImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class JdbcHikariRepository implements Closeable {

	private final HikariDataSource dataSource;
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public JdbcHikariRepository(HikariConfig info) {
		this.dataSource = new HikariDataSource(info);
		this.jdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
	}

	public JdbcHikariRepository(NamedParameterJdbcTemplate jdbcTemplate) {
		this.dataSource = null;
		this.jdbcTemplate = jdbcTemplate;
	}

	public <T> JdbcImpl<T> jdbc(Class<T> clazz) {
		return new JdbcImpl<>(clazz) {

			@Override
			public NamedParameterJdbcTemplate jdbcTemplate() {
				return jdbcTemplate;
			}

		};
	}

	public NamedParameterJdbcTemplate jdbcTemplate() {
		return this.jdbcTemplate;
	}

	@Override
	public void close() throws IOException {
		this.dataSource.close();
	}

}
