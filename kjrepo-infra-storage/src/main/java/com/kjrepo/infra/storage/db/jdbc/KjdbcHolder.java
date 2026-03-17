package com.kjrepo.infra.storage.db.jdbc;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.annimon.stream.Optional;
import com.kjrepo.infra.storage.db.model.KdbDialect;
import com.kjrepo.infra.storage.db.utils.DB_dialect_utils;

public class KjdbcHolder implements AutoCloseable {

	public static KjdbcHolder of(NamedParameterJdbcTemplate jdbcTemplate) {
		return of(jdbcTemplate.getJdbcTemplate().getDataSource(), "");
	}

	public static KjdbcHolder of(DataSource dataSource, String tag) {
		return new KjdbcHolder(dataSource,
				Optional.ofNullable(tag).filter(StringUtils::isNotEmpty).map(t -> t + ".").orElse(""));
	}

	private final DataSource dataSource;
	private final String tag;
	private final NamedParameterJdbcOperations jdbcTemplate;
	private final KdbDialect dialect;

	protected KjdbcHolder(DataSource dataSource, String tag) {
		this.dataSource = dataSource;
		this.tag = tag;
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.dialect = DB_dialect_utils.dialect(dataSource);
	}

	public String tag() {
		return tag;
	}

	public NamedParameterJdbcOperations jdbcTemplate() {
		return jdbcTemplate;
	}

	public KdbDialect dialect() {
		return dialect;
	}

	@Override
	public void close() throws Exception {
		if (this.dataSource instanceof AutoCloseable) {
			((AutoCloseable) this.dataSource).close();
		}
	}
}
