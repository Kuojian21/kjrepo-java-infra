package com.kjrepo.infra.storage.db.jdbc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.utils.StringUtils;
import com.kjrepo.infra.storage.db.model.KdbDialect;
import com.kjrepo.infra.storage.db.model.KdbIndex;
import com.kjrepo.infra.storage.db.model.KdbModel;
import com.kjrepo.infra.storage.db.model.KdbProperty;
import com.kjrepo.infra.storage.db.sql.SqlBuilder;
import com.kjrepo.infra.storage.db.sql.SqlDeleteBuilder;
import com.kjrepo.infra.storage.db.sql.SqlValueExpr;
import com.kjrepo.infra.storage.db.sql.SqlInsertBuilder;
import com.kjrepo.infra.storage.db.sql.SqlSelectBuilder;
import com.kjrepo.infra.storage.db.sql.SqlUpdateBuilder;
import com.kjrepo.infra.storage.db.sql.SqlWhereBuilder;
import com.kjrepo.infra.storage.db.utils.KdbUtils;

public interface Kjdbc<T> {

	Logger logger = LoggerUtils.logger(Kjdbc.class);

	default int insert(T data) {
		if (data == null) {
			return 0;
		}
		return insert(Lists.newArrayList(data), false);
	}

	default int insert(List<T> data) {
		if (data == null || data.size() == 0) {
			return 0;
		}
		return insert(data, false);
	}

	default int insert(T data, boolean ignore) {
		if (data == null) {
			return 0;
		}
		return insert(Lists.newArrayList(data), ignore);
	}

	default int insert(List<T> data, boolean ignore) {
		if (data == null || data.size() == 0) {
			return 0;
		}
		return Stream.of(Lists.partition(data, 100)).mapToInt(models -> {
			try {
				return this.insert(SqlBuilder.insert(ignore).model(models));
			} catch (UncategorizedSQLException e) {
				if (ignore && KdbUtils.checkIntegrityConstraint(e)) {
					if (models.size() > 1) {
						return Stream.of(models).mapToInt(m -> this.insert(m, ignore)).sum();
					}
					return 0;
				}
				throw e;
			}
		}).sum();
	}

	default int upsert(T data, List<String> columns) {
		if (data == null) {
			return 0;
		}
		return upsert(Lists.newArrayList(data), columns);
	}

	default int upsert(List<T> data, List<String> columns) {
		return upsert(data, Stream.of(columns)
				.collect(Collectors.toMap(c -> c, c -> SqlValueExpr.of(SqlValueExpr.sqlValueExprInsert(c)))));
	}

	default int upsert(T data, Map<String, Object> update) {
		if (data == null) {
			return 0;
		}
		return upsert(Lists.newArrayList(data), update);
	}

	default int upsert(List<T> data, Map<String, Object> update) {
		if (data == null || data.size() == 0) {
			return 0;
		}
		Map<String, Object> update1 = Stream.of(update).collect(Collectors.toMap(en -> en.getKey(), en -> {
			Object val = en.getValue();
			if (val instanceof SqlValueExpr) {
				String expr = ((SqlValueExpr) val).expr();
				Set<String> cols = StringUtils.extract(expr, SqlValueExpr.INSERT_VALUE_PREFIX,
						SqlValueExpr.INSERT_VALUE_SUFFIX);
				return SqlValueExpr.of(
						StringSubstitutor.replace(expr, Stream.of(cols).collect(Collectors.toMap(col -> col, col -> {
							switch (dialect()) {
							case MySQL:
								return "values(" + col + ")";
							case PostgreSQL:
								return "EXCLUDED." + col;
							case H2:
							case Sqlite:
							case SQLServer:
							case Oracle:
							default:
								return col;
							}
						})), SqlValueExpr.INSERT_VALUE_PREFIX, SqlValueExpr.INSERT_VALUE_SUFFIX));
			}
			return val;
		}));
		return Stream.of(Lists.partition(data, 100)).mapToInt(models -> {
			try {
				return this.insert(SqlBuilder.upsert(update1).model(models));
			} catch (UncategorizedSQLException e) {
				if (KdbUtils.checkIntegrityConstraint(e)) {
					if (models.size() > 1) {
						return Stream.of(models).mapToInt(m -> this.upsert(m, update)).sum();
					}
					T m = models.get(0);
					Map<String, Object> update2 = Stream.of(update).collect(Collectors.toMap(en -> en.getKey(), en -> {
						Object val = en.getValue();
						if (val instanceof SqlValueExpr) {
							String expr = ((SqlValueExpr) val).expr();
							return SqlValueExpr.of(expr,
									Stream.of(StringUtils.extract(expr, SqlValueExpr.INSERT_VALUE_PREFIX,
											SqlValueExpr.INSERT_VALUE_SUFFIX))
											.collect(Collectors.toMap(col -> col,
													col -> model().getProperty(col).readAndCast(m))));
						}
						return val;
					}));
					for (KdbIndex kdbIndex : model().uniIndexes()) {
						Map<String, Object> params = Stream.of(kdbIndex.columns())
								.collect(Collectors.toMap(c -> c, c -> model().getProperty(c).readAndCast(m)));
						int rtn = update(update2, params);
						if (rtn > 0) {
							return rtn;
						}
					}
					for (KdbProperty p : model().uniProperties()) {
						Map<String, Object> params = ImmutableMap.of(p.column(), p.readAndCast(m));
						int rtn = update(update2, params);
						if (rtn > 0) {
							return rtn;
						}
					}
					return 0;
				}
				throw e;
			}
		}).sum();
	}

	default int update(Map<String, Object> update, Map<String, Object> params) {
		return update(update, SqlWhereBuilder.and().expr(params));
	}

	default int update(Map<String, Object> update, SqlWhereBuilder where) {
		return this.update(SqlBuilder.update(update).where(where));
	}

	default int delete(Map<String, Object> params) {
		return delete(SqlWhereBuilder.and().expr(params));
	}

	default int delete(SqlWhereBuilder where) {
		return this.delete(SqlBuilder.delete().where(where));
	}

	default List<T> select(Map<String, Object> params) {
		return select(SqlWhereBuilder.and().expr(params));
	}

	default List<T> select(SqlWhereBuilder where) {
		return this.select(SqlBuilder.select().where(where));
	}

	int insert(SqlInsertBuilder sqlBuilder);

	List<T> select(SqlSelectBuilder sqlBuilder);

	int update(SqlUpdateBuilder sqlBuilder);

	int delete(SqlDeleteBuilder sqlBuilder);

	NamedParameterJdbcOperations jdbcTemplate();

	KdbModel model();

	KdbDialect dialect();

	String table();

}
