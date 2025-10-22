package com.kjrepo.infra.storage.db.jdbc;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.storage.db.model.KdbModel;
import com.kjrepo.infra.storage.db.sql.SqlBuilder;
import com.kjrepo.infra.storage.db.sql.SqlWhereBuilder;

public abstract class KjdbcImpl<T> implements Kjdbc<T> {

	protected final Logger logger = LoggerUtils.logger(getClass());

	private final StringSubstitutor format = new StringSubstitutor(key -> "?", ":v", "v",
			StringSubstitutor.DEFAULT_ESCAPE);
	private final KdbModel kdbModel;
	private final String table;
	private final RowMapper<T> mapper;

	public KjdbcImpl(Class<T> clazz) {
		this(clazz, null);
	}

	public KjdbcImpl(Class<T> clazz, String suffix) {
		this.kdbModel = KdbModel.of(clazz);
		this.table = StringUtils.isNotEmpty(suffix) ? kdbModel.table() + "_" + suffix : kdbModel.table();
		this.mapper = new BeanPropertyRowMapper<>(clazz);
	}

	@Override
	public int insert(List<T> data) {
		if (data == null || data.size() == 0) {
			return 0;
		}
		SqlBuilder sqlBuilder = SqlBuilder.insert(this.kdbModel, this.table).model(data);
		logger.debug("sql:{}", format.replace(sqlBuilder.sql()));
		return this.jdbcTemplate().update(sqlBuilder.sql(), sqlBuilder.valueMap());
	}

	@Override
	public List<T> select(SqlWhereBuilder sqlWhereBuilder) {
		SqlBuilder sqlBuilder = SqlBuilder.select(this.kdbModel, this.table).where(sqlWhereBuilder);
		logger.debug("sql:{}", format.replace(sqlBuilder.sql()));
		return this.jdbcTemplate().query(sqlBuilder.sql(), sqlBuilder.valueMap(), this.mapper);
	}

	@Override
	public int update(Map<String, Object> setValues, SqlWhereBuilder sqlWhereBuilder) {
		SqlBuilder sqlBuilder = SqlBuilder.update(this.kdbModel, this.table, setValues).where(sqlWhereBuilder);
		logger.debug("sql:{}", format.replace(sqlBuilder.sql()));
		return this.jdbcTemplate().update(sqlBuilder.sql(), sqlBuilder.valueMap());
	}

	@Override
	public int delete(SqlWhereBuilder sqlWhereBuilder) {
		SqlBuilder sqlBuilder = SqlBuilder.delete(this.kdbModel, this.table).where(sqlWhereBuilder);
		logger.debug("sql:{}", format.replace(sqlBuilder.sql()));
		return this.jdbcTemplate().update(sqlBuilder.sql(), sqlBuilder.valueMap());
	}

	@Override
	public KdbModel model() {
		return this.kdbModel;
	}
}