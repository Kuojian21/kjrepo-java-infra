package com.kjrepo.infra.storage.db.jdbc;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.RowMapper;

import com.google.common.base.Stopwatch;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.storage.db.model.KdbDialect;
import com.kjrepo.infra.storage.db.model.KdbModel;
import com.kjrepo.infra.storage.db.sql.SqlDeleteBuilder;
import com.kjrepo.infra.storage.db.sql.SqlInsertBuilder;
import com.kjrepo.infra.storage.db.sql.SqlSelectBuilder;
import com.kjrepo.infra.storage.db.sql.SqlUpdateBuilder;
import com.kjrepo.infra.storage.db.utils.KdbUtils;

public abstract class KjdbcImpl<T> implements Kjdbc<T> {

	private final StringSubstitutor format = new StringSubstitutor(key -> "?", ":v", "v",
			StringSubstitutor.DEFAULT_ESCAPE);
	private final KdbModel kdbModel;
	private final String table;
	private final RowMapper<T> mapper;
	private final LazySupplier<KdbDialect> dialect = LazySupplier.wrap(() -> {
		return this.jdbcTemplate().getJdbcOperations()
				.execute((ConnectionCallback<KdbDialect>) conn -> KdbUtils.dialect(conn));
	});

	public KjdbcImpl(Class<T> clazz) {
		this(clazz, null);
	}

	public KjdbcImpl(Class<T> clazz, String suffix) {
		this.kdbModel = KdbModel.of(clazz);
		this.table = StringUtils.isNotEmpty(suffix) ? kdbModel.table() + "_" + suffix : kdbModel.table();
		this.mapper = new BeanPropertyRowMapper<>(clazz);
	}

	@Override
	public int insert(SqlInsertBuilder sqlBuilder) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		String sql = sqlBuilder.init(table(), model(), dialect()).sql();
		logger.debug("insert-sql:{}", format.replace(sql));
		int rtn = this.jdbcTemplate().update(sql, sqlBuilder.valueMap());
		logger.debug("insert-rtn:{} elapsed:{}", rtn,
				N_humanUtils.formatMills(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
		return rtn;
	}

	@Override
	public List<T> select(SqlSelectBuilder sqlBuilder) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		String sql = sqlBuilder.init(table(), model(), dialect()).sql();
		logger.debug("select-sql:{}", format.replace(sql));
		List<T> rtn = this.jdbcTemplate().query(sql, sqlBuilder.valueMap(), this.mapper);
		logger.debug("select-rtn.size:{} elapsed:{}", rtn.size(),
				N_humanUtils.formatMills(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
		return rtn;
	}

	@Override
	public int update(SqlUpdateBuilder sqlBuilder) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		String sql = sqlBuilder.init(table(), model(), dialect()).sql();
		logger.debug("update-sql:{}", format.replace(sql));
		int rtn = this.jdbcTemplate().update(sql, sqlBuilder.valueMap());
		logger.debug("update-rtn:{} elapsed:{}", rtn,
				N_humanUtils.formatMills(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
		return rtn;
	}

	@Override
	public int delete(SqlDeleteBuilder sqlBuilder) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		String sql = sqlBuilder.init(table(), model(), dialect()).sql();
		logger.debug("delete-sql:{}", format.replace(sql));
		int rtn = this.jdbcTemplate().update(sql, sqlBuilder.valueMap());
		logger.debug("delete-rtn:{} elapsed:{}", rtn,
				N_humanUtils.formatMills(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
		return rtn;
	}

	@Override
	public final KdbModel model() {
		return this.kdbModel;
	}

	@Override
	public KdbDialect dialect() {
		return this.dialect.get();
	}

	@Override
	public String table() {
		return this.table;
	}
}