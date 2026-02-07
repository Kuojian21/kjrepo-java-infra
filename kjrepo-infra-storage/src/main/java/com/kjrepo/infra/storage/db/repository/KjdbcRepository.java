package com.kjrepo.infra.storage.db.repository;

import java.io.Closeable;
import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.ConnectionCallback;

import com.kjrepo.infra.storage.db.jdbc.Kjdbc;
import com.kjrepo.infra.storage.db.jdbc.KjdbcImpl;
import com.kjrepo.infra.storage.db.model.KdbDialect;
import com.kjrepo.infra.storage.db.utils.KdbUtils;

public class KjdbcRepository implements Closeable {

	private final KdbDialect dialect;
	private final NamedParameterJdbcTemplate jdbcTemplate;

	public KjdbcRepository(DataSource dataSource) {
		this(new NamedParameterJdbcTemplate(dataSource));
	}

	public KjdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.dialect = jdbcTemplate.getJdbcOperations()
				.execute((ConnectionCallback<KdbDialect>) conn -> KdbUtils.dialect(conn));
	}

	public <T> Kjdbc<T> jdbc(Class<T> clazz) {
		return new KjdbcImpl<>(clazz) {
			@Override
			public NamedParameterJdbcTemplate jdbcTemplate() {
				return jdbcTemplate;
			}

			@Override
			public KdbDialect dialect() {
				return dialect;
			}
		};
	}

	public KdbDialect dialect() {
		return this.dialect;
	}

	public NamedParameterJdbcTemplate jdbcTemplate() {
		return this.jdbcTemplate;
	}

	@Override
	public void close() throws IOException {
		DataSource dataSource = this.jdbcTemplate.getJdbcTemplate().getDataSource();
		if (dataSource instanceof Closeable) {
			((Closeable) dataSource).close();
		}
	}

}
