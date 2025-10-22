package com.kjrepo.infra.storage.db.repository;

import java.io.Closeable;
import java.io.IOException;

import javax.sql.DataSource;

import org.hibernate.dialect.Dialect;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.kjrepo.infra.storage.db.jdbc.Kjdbc;
import com.kjrepo.infra.storage.db.jdbc.KjdbcImpl;
import com.kjrepo.infra.storage.db.utils.KdbUtils;

public class KjdbcRepository implements Closeable {

	private final DataSource dataSource;
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public KjdbcRepository(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
	}

	public KjdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
		this.dataSource = null;
		this.jdbcTemplate = jdbcTemplate;
	}

	public <T> Kjdbc<T> jdbc(Class<T> clazz) {
		return new KjdbcImpl<>(clazz) {

			@Override
			public NamedParameterJdbcTemplate jdbcTemplate() {
				return jdbcTemplate;
			}

		};
	}

	public Dialect dialect() {
		return KdbUtils.dialect(dataSource);
	}

	public NamedParameterJdbcTemplate jdbcTemplate() {
		return this.jdbcTemplate;
	}

	@Override
	public void close() throws IOException {
		if (this.dataSource instanceof Closeable) {
			((Closeable) this.dataSource).close();
		}
	}

}
