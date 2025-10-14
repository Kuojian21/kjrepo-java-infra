package com.kjrepo.infra.storage.db.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.storage.db.dialect.DialectUtils;
import com.kjrepo.infra.storage.db.model.Model;
import com.kjrepo.infra.storage.db.sql.SqlBuilder;
import com.kjrepo.infra.storage.db.sql.SqlWhereBuilder;

public abstract class JdbcImpl<T> implements Jdbc<T> {

	protected final Logger logger = LoggerUtils.logger(getClass());

	private final StringSubstitutor format = new StringSubstitutor(key -> "?", ":v", "v",
			StringSubstitutor.DEFAULT_ESCAPE);
	private final Model model;
	private final String table;
	private final RowMapper<T> mapper;

	public JdbcImpl(Class<T> clazz) {
		this(clazz, null);
	}

	public JdbcImpl(Class<T> clazz, String suffix) {
		this.model = Model.of(clazz);
		this.table = StringUtils.isNotEmpty(suffix) ? model.table() + "_" + suffix : model.table();
		this.mapper = new BeanPropertyRowMapper<>(clazz);
	}

	@Override
	public int insert(List<T> data) {
		SqlBuilder sqlBuilder = SqlBuilder.insert(this.model, this.table).model(data);
		logger.debug("sql:{}", format.replace(sqlBuilder.sql()));
		return this.jdbcTemplate().update(sqlBuilder.sql(), sqlBuilder.valueMap());
	}

	@Override
	public List<T> select(SqlWhereBuilder sqlWhereBuilder) {
		SqlBuilder sqlBuilder = SqlBuilder.select(this.model, this.table).where(sqlWhereBuilder);
		logger.debug("sql:{}", format.replace(sqlBuilder.sql()));
		return this.jdbcTemplate().query(sqlBuilder.sql(), sqlBuilder.valueMap(), this.mapper);
	}

	@Override
	public int update(Map<String, Object> setValues, SqlWhereBuilder sqlWhereBuilder) {
		SqlBuilder sqlBuilder = SqlBuilder.update(this.model, this.table).set(setValues).where(sqlWhereBuilder);
		logger.debug("sql:{}", format.replace(sqlBuilder.sql()));
		return this.jdbcTemplate().update(sqlBuilder.sql(), sqlBuilder.valueMap());
	}

	@Override
	public int delete(SqlWhereBuilder sqlWhereBuilder) {
		SqlBuilder sqlBuilder = SqlBuilder.delete(this.model, this.table).where(sqlWhereBuilder);
		logger.debug("sql:{}", format.replace(sqlBuilder.sql()));
		return this.jdbcTemplate().update(sqlBuilder.sql(), sqlBuilder.valueMap());
	}

	@Override
	public Model model() {
		return this.model;
	}

	public boolean create() {
		if (this.jdbcTemplate() instanceof NamedParameterJdbcTemplate) {
			NamedParameterJdbcTemplate jt = (NamedParameterJdbcTemplate) this.jdbcTemplate();
			DataSource datasource = jt.getJdbcTemplate().getDataSource();
			try (Connection conn = datasource.getConnection()) {
				try (Statement stmt = conn.createStatement()) {
					return stmt.execute(this.model.toCreateSql(DialectUtils.dialect(conn)));
				}
			} catch (SQLException e) {
				throw new IllegalStateException("", e);
			}
		}
		throw new RuntimeException("unsupported!!!");
	}

}