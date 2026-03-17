package com.kjrepo.infra.storage.db.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.SqlTypes;
import org.slf4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.storage.db.dialect.SqliteDialect;
import com.kjrepo.infra.storage.db.model.KdbColumn;
import com.kjrepo.infra.storage.db.model.KdbDialect;
import com.kjrepo.infra.storage.db.model.KdbModel;
import com.kjrepo.infra.storage.db.model.KdbProperty;

public class DB_ddl_utils {

	private static final Logger logger = LoggerUtils.logger(DB_ddl_utils.class);

	public static Map<Class<?>, Method> methods = Maps.newConcurrentMap();

	public static void createTableIfNotExists(DataSource dataSource, KdbModel model) throws SQLException {
		try (Connection conn = dataSource.getConnection()) {
			createTableIfNotExists(conn, model);
		}
	}

	public static void createTableIfNotExists(NamedParameterJdbcOperations jdbcTemplate, KdbModel model) {
		jdbcTemplate.getJdbcOperations().execute(new ConnectionCallback<Void>() {
			@Override
			public Void doInConnection(Connection conn) throws SQLException, DataAccessException {
				createTableIfNotExists(conn, model);
				return null;
			}
		});
	}

	public static void createTableIfNotExists(Connection conn, KdbModel model) throws SQLException {
		KdbDialect dialect = DB_dialect_utils.dialect(conn);
		DatabaseMetaData meta = conn.getMetaData();
		try (ResultSet rs = meta.getTables(null, null, model.table().toLowerCase(), new String[] { "TABLE" })) {
			if (rs.next()) {
				logger.info("The table:{} has already exists!!!", model.table());
				return;
			}
		}
		try (ResultSet rs = meta.getTables(null, null, model.table().toUpperCase(), new String[] { "TABLE" })) {
			if (rs.next()) {
				logger.info("The table:{} has already exists!!!", model.table());
				return;
			}
		}
		try (Statement stmt = conn.createStatement()) {
			String ctSql = toCreateTableSql(model, dialect);
			logger.info("sql:{}", ctSql);
			stmt.execute(ctSql);
			for (String ciSql : toCreateIndexSql(model, dialect)) {
				logger.info("sql:{}", ciSql);
				stmt.execute(ciSql);
			}
		}
	}

	public static String toCreateTableSql(Class<?> clazz, KdbDialect dialect) {
		return toCreateTableSql(KdbModel.of(clazz), dialect, true);
	}

	public static String toCreateTableSql(KdbModel model, KdbDialect dialect) {
		return toCreateTableSql(model, dialect, true);
	}

	public static String toCreateTableSql(KdbModel model, KdbDialect dialect, boolean ifNotExists) {
		StringBuilder sql = new StringBuilder();
		sql.append(dialect.dialect().getCreateTableString());
		if (ifNotExists && DB_ddl_utils.supportCreateIfNotExists(dialect)) {
			sql.append(" IF NOT EXISTS");
		}
		sql.append(" " + model.table() + "(\n\t");
		sql.append(StringUtils.join(Stream.of(model.properties()).map(py -> {
			StringBuilder psql = new StringBuilder();
			psql.append(py.column()).append(" ");
			if (StringUtils.isNotEmpty(py.definition())) {
				psql.append(py.definition());
			} else {
				psql.append(DB_ddl_utils.columnType(dialect.dialect(), py.type(), py.kdbColumn()));
				if (!py.nullable()) {
					psql.append(" not null");
				}
				if (py.primary()) {
					psql.append(" primary key");
				}
				if (py.unique()) {
					psql.append(" unique");
				}
				if (py.identity()) {
					psql.append(
							" " + dialect.dialect().getIdentityColumnSupport().getIdentityColumnString(Types.BIGINT));
				}
				if (StringUtils.isNotEmpty(py.comment())) {
					psql.append(" " + dialect.dialect().getColumnComment(py.comment()));
				}
			}
			return psql.toString();
		}).toList(), ",\n\t"));
		sql.append("\n);");
		return sql.toString();
	}

	public static List<String> toCreateIndexSql(Class<?> clazz, KdbDialect dialect) {
		return toCreateIndexSql(KdbModel.of(clazz), dialect);
	}

	public static List<String> toCreateIndexSql(KdbModel model, KdbDialect dialect) {
		if (model.kdbTable() != null && model.kdbTable().indexes() != null && model.kdbTable().indexes().length > 0) {
			return Stream.of(model.kdbTable().indexes()).map(index -> {
				StringBuilder sql = new StringBuilder();
				switch (index.type()) {
				case PRI:
					sql.append("ALTER TABLE " + model.table() + " ");
					break;
				case UNI:
					sql.append("CREATE UNIQUE INDEX ").append(index.name()).append(" ON ").append(model.table());
					break;
				case IND:
					sql.append("CREATE INDEX ").append(index.name()).append(" ON ").append(model.table());
				default:
				}
				sql.append("(")
						.append(StringUtils.join(
								Stream.of(index.columns()).map(model::getProperty).map(KdbProperty::column).toList(),
								","))
						.append(")");
				return sql.toString();
			}).toList();
		}
		return Lists.newArrayList();
	}

	public static boolean detectTableExists(Connection conn, String table) {
		try {
			String dname = conn.getMetaData().getDatabaseProductName();
			String dversion = conn.getMetaData().getDatabaseProductVersion();
			logger.info("database:{} version:{}", dname, dversion);
			try (Statement stmt = conn.createStatement()) {
				String sql = "";
				if (dname.contains("MySQL")) {
					sql = "SHOW TABLES LIKE ${table}";
				} else if (dname.contains("PostgreSQL")) {
					sql = "SELECT tablename FROM pg_catalog.pg_tables WHERE schemaname = 'public' AND tablename = ${table}";
				} else if (dname.contains("Oracle")) {
					sql = "SELECT table_name FROM USER_TABLES WHERE table_name = ${table}";
				} else if (dname.contains("SQL Server")) {
					sql = "SELECT name FROM sys.tables WHERE name = ${table}";
				} else if (dname.contains("H2")) {
					sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME = ${table}";
				} else if (dname.contains("SQLite")) {
					sql = "SELECT name FROM sqlite_master WHERE type='table' AND name = ${table}";
				} else {
					throw new RuntimeException("unknown database!!!");
				}
				try (ResultSet rs = stmt.executeQuery(
						StringSubstitutor.replace(sql, ImmutableMap.of("table", "'" + table.toLowerCase() + "'")))) {
					if (rs.next()) {
						return true;
					}
				}
				try (ResultSet rs = stmt.executeQuery(
						StringSubstitutor.replace(sql, ImmutableMap.of("table", "'" + table.toUpperCase() + "'")))) {
					if (rs.next()) {
						return true;
					}
				}
				return false;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean supportCreateIfNotExists(KdbDialect dialect) {
		return dialect == KdbDialect.MySQL || dialect == KdbDialect.PostgreSQL || dialect == KdbDialect.H2
				|| dialect == KdbDialect.Sqlite;
	}

	public static String columnType(Dialect dialect, Class<?> type, KdbColumn kdbColumn) {
		if (kdbColumn != null && kdbColumn.identity() && dialect instanceof SqliteDialect) {
			return DB_ddl_utils.columnType(dialect, SqlTypes.INTEGER);
		} else if (type == int.class || type == Integer.class) {
			return DB_ddl_utils.columnType(dialect, SqlTypes.INTEGER);
		} else if (type == byte.class || type == Byte.class) {
			return DB_ddl_utils.columnType(dialect, SqlTypes.TINYINT);
		} else if (type == short.class || type == Short.class) {
			return DB_ddl_utils.columnType(dialect, SqlTypes.SMALLINT);
		} else if (type == long.class || type == Long.class) {
			return DB_ddl_utils.columnType(dialect, SqlTypes.BIGINT);
		} else if (type == float.class || type == Float.class) {
			return DB_ddl_utils.columnType(dialect, SqlTypes.FLOAT);
		} else if (type == double.class || type == Double.class || type == BigDecimal.class) {
			return DB_ddl_utils.columnType(dialect, SqlTypes.DECIMAL)
					.replace("$p", Optional.ofNullable(kdbColumn).map(KdbColumn::precision).orElse(12) + "")
					.replace("$s", Optional.ofNullable(kdbColumn).map(KdbColumn::scope).orElse(2) + "");
		} else if (type == boolean.class || type == Boolean.class) {
			return DB_ddl_utils.columnType(dialect, SqlTypes.BOOLEAN);
		} else if (type == String.class) {
			return DB_ddl_utils.columnType(dialect, SqlTypes.VARCHAR).replace("$l",
					Optional.ofNullable(kdbColumn).map(KdbColumn::length).orElse(60) + "");
		} else if (type.isEnum()) {
			return DB_ddl_utils.columnType(dialect, SqlTypes.VARCHAR).replace("$l", "30");
		} else if (type == Timestamp.class) {
			return DB_ddl_utils.columnType(dialect, SqlTypes.TIMESTAMP);
		} else if (java.util.Date.class.isAssignableFrom(type)) {
			return DB_ddl_utils.columnType(dialect, SqlTypes.DATE);
		}
		throw new RuntimeException("unknown data type:" + type.getName());
	}

	public static String columnType(Dialect dialect, int type) {
		try {
			return (String) methods.computeIfAbsent(dialect.getClass(), key -> {
				try {
					Method method = dialect.getClass().getDeclaredMethod("columnType", int.class);
					method.setAccessible(true);
					return method;
				} catch (NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}).invoke(dialect, new Object[] { type });
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
