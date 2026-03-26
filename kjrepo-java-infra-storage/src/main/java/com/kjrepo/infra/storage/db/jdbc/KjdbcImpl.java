package com.kjrepo.infra.storage.db.jdbc;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import com.annimon.stream.Optional;
import com.google.common.base.Stopwatch;
import com.kjrepo.infra.perf.utils.PerfUtils;
import com.kjrepo.infra.storage.db.model.KdbModel;
import com.kjrepo.infra.storage.db.sql.SqlDeleteBuilder;
import com.kjrepo.infra.storage.db.sql.SqlInsertBuilder;
import com.kjrepo.infra.storage.db.sql.SqlSelectBuilder;
import com.kjrepo.infra.storage.db.sql.SqlUpdateBuilder;

public abstract class KjdbcImpl<T> implements Kjdbc<T> {

	private static final StringSubstitutor format = new StringSubstitutor(key -> "?", ":v", "v",
			StringSubstitutor.DEFAULT_ESCAPE);

	private final KdbModel model;
	private final String table;
	private final RowMapper<T> mapper;

	public KjdbcImpl(Class<T> clazz) {
		this(clazz, null);
	}

	public KjdbcImpl(Class<T> clazz, String suffix) {
		this.model = KdbModel.of(clazz);
		this.table = model.table()
				+ Optional.ofNullable(suffix).filter(StringUtils::isNotEmpty).map(s -> "_" + s).orElse("");
		this.mapper = new BeanPropertyRowMapper<>(clazz);
	}

	@Override
	public int insert(SqlInsertBuilder sqlBuilder) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		KjdbcHolder holder = this.holder(true);
		String e_sql = sqlBuilder.init(table(), model(), holder.dialect()).sql();
		String f_sql = format.replace(e_sql);
		int rtn;
		try {
			logger.debug("insert-sql:{}", f_sql);
			rtn = holder.jdbcTemplate().update(e_sql, sqlBuilder.valueMap());
			PerfUtils.perf(PerfUtils.N_storage_kjdbc, holder.tag() + "exec", f_sql).count(1)
					.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
		} catch (DataAccessException e) {
			PerfUtils.perf(PerfUtils.N_storage_kjdbc, holder.tag() + e.getClass().getSimpleName(), f_sql).count(1)
					.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
			throw e;
		}
		return rtn;
	}

	@Override
	public List<T> select(SqlSelectBuilder sqlBuilder, boolean master) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		KjdbcHolder holder = this.holder(master);
		String e_sql = sqlBuilder.init(table(), model(), holder.dialect()).sql();
		String f_sql = format.replace(e_sql);
		List<T> rtn;
		try {
			logger.debug("select-sql:{}", f_sql);
			rtn = holder.jdbcTemplate().query(e_sql, sqlBuilder.valueMap(), this.mapper);
			PerfUtils.perf(PerfUtils.N_storage_kjdbc, holder.tag() + "exec", f_sql, master + "").count(1)
					.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
		} catch (DataAccessException e) {
			PerfUtils.perf(PerfUtils.N_storage_kjdbc, holder.tag() + e.getClass().getSimpleName(), f_sql, master + "")
					.count(1).micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
			throw e;
		}
		return rtn;
	}

	@Override
	public int update(SqlUpdateBuilder sqlBuilder) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		KjdbcHolder holder = this.holder(true);
		String e_sql = sqlBuilder.init(table(), model(), holder.dialect()).sql();
		String f_sql = format.replace(e_sql);
		int rtn;
		try {
			logger.debug("update-sql:{}", f_sql);
			rtn = holder.jdbcTemplate().update(e_sql, sqlBuilder.valueMap());
			PerfUtils.perf(PerfUtils.N_storage_kjdbc, holder.tag() + "exec", f_sql).count(1)
					.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
		} catch (DataAccessException e) {
			PerfUtils.perf(PerfUtils.N_storage_kjdbc, holder.tag() + e.getClass().getSimpleName(), f_sql).count(1)
					.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
			throw e;
		}
		return rtn;
	}

	@Override
	public int delete(SqlDeleteBuilder sqlBuilder) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		KjdbcHolder holder = this.holder(true);
		String e_sql = sqlBuilder.init(table(), model(), holder.dialect()).sql();
		String f_sql = format.replace(e_sql);
		int rtn;
		try {
			logger.debug("delete-sql:{}", f_sql);
			rtn = holder.jdbcTemplate().update(e_sql, sqlBuilder.valueMap());
			PerfUtils.perf(PerfUtils.N_storage_kjdbc, holder.tag() + "exec", f_sql).count(1)
					.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
		} catch (DataAccessException e) {
			PerfUtils.perf(PerfUtils.N_storage_kjdbc, holder.tag() + e.getClass().getSimpleName(), f_sql).count(1)
					.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
			throw e;
		}
		return rtn;
	}

	@Override
	public final KdbModel model() {
		return this.model;
	}

	@Override
	public final String table() {
		return this.table;
	}
}