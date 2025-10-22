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
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.OracleDialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.type.SqlTypes;
import org.slf4j.Logger;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.storage.db.dialect.H2Dialect;
import com.kjrepo.infra.storage.db.dialect.SqliteDialect;
import com.kjrepo.infra.storage.db.model.KdbColumn;
import com.kjrepo.infra.storage.db.model.KdbModel;
import com.kjrepo.infra.storage.db.model.KdbProperty;

public class KdbUtils {

	private static final Logger logger = LoggerUtils.logger(KdbUtils.class);

	public static Map<Class<?>, Method> methods = Maps.newConcurrentMap();

	public static void createTableIfNotExists(NamedParameterJdbcTemplate jdbcTemplate, KdbModel model) {
		createTableIfNotExists(jdbcTemplate.getJdbcTemplate().getDataSource(), model);
	}

	public static void createTableIfNotExists(DataSource datasource, KdbModel model) {
		try (Connection conn = datasource.getConnection()) {
			Dialect dialect = KdbUtils.dialect(conn);
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
		} catch (SQLException e) {
			throw new IllegalStateException("", e);
		}
	}

	public static String toCreateTableSql(Class<?> clazz, Dialect dialect) {
		return toCreateTableSql(KdbModel.of(clazz), dialect, true);
	}

	public static String toCreateTableSql(KdbModel model, Dialect dialect) {
		return toCreateTableSql(model, dialect, true);
	}

	public static String toCreateTableSql(KdbModel model, Dialect dialect, boolean ifNotExists) {
		StringBuilder sql = new StringBuilder();
		sql.append(dialect.getCreateTableString());
		if (ifNotExists && KdbUtils.supportCreateIfNotExists(dialect)) {
			sql.append(" IF NOT EXISTS");
		}
		sql.append(" " + model.table() + "(\n\t");
		sql.append(StringUtils.join(Stream.of(model.properties()).map(py -> {
			StringBuilder psql = new StringBuilder();
			psql.append(py.column()).append(" ");
			if (py.kdbColumn() != null && StringUtils.isNotEmpty(py.kdbColumn().definition())) {
				psql.append(py.kdbColumn().definition());
			} else {
				psql.append(KdbUtils.columnType(dialect, py.type(), py.kdbColumn()));
				if (py.kdbColumn() != null && !py.kdbColumn().nullable()) {
					psql.append(" not null");
				}
				if (py.kdbColumn() != null && py.kdbColumn().primary()) {
					psql.append(" primary key");
				}
				if (py.kdbColumn() != null && py.kdbColumn().unique()) {
					psql.append(" unique");
				}
				if (py.kdbColumn() != null && py.kdbColumn().identity()) {
					psql.append(" " + dialect.getIdentityColumnSupport().getIdentityColumnString(Types.BIGINT));
				}
				if (py.kdbColumn() != null && StringUtils.isNotEmpty(py.kdbColumn().comment())) {
					psql.append(" " + dialect.getColumnComment(py.column()));
				}
			}
			return psql.toString();
		}).toList(), ",\n\t"));
		sql.append("\n);");
		return sql.toString();
	}

	public static List<String> toCreateIndexSql(Class<?> clazz, Dialect dialect) {
		return toCreateIndexSql(KdbModel.of(clazz), dialect);
	}

	public static List<String> toCreateIndexSql(KdbModel model, Dialect dialect) {
		if (model.kdbTable() != null && model.kdbTable().indexes() != null && model.kdbTable().indexes().length > 0) {
			return Stream.of(model.kdbTable().indexes()).map(index -> {
				StringBuilder sql = new StringBuilder();
				switch (index.type()) {
				case PRIMARY:
					sql.append("ALTER TABLE " + model.table() + " ");
					break;
				case UNIQUE:
					sql.append("CREATE UNIQUE INDEX ").append(index.name()).append(" ON ").append(model.table());
					break;
				case INDEX:
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

	public static Dialect dialect(DataSource datasource) {
		try (Connection conn = datasource.getConnection()) {
			return dialect(conn);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static Dialect dialect(Connection conn) {
		try {
			String dname = conn.getMetaData().getDatabaseProductName();
			String dversion = conn.getMetaData().getDatabaseProductVersion();
			logger.info("database:{} version:{}", dname, dversion);
			if (dname.contains("MySQL")) {
				return new MySQLDialect();
			} else if (dname.contains("PostgreSQL")) {
				return new PostgreSQLDialect();
			} else if (dname.contains("Oracle")) {
				return new OracleDialect();
			} else if (dname.contains("SQL Server")) {
				return new SQLServerDialect();
			} else if (dname.contains("H2")) {
				return new H2Dialect();
			} else if (dname.contains("SQLite")) {
				return new SqliteDialect();
			} else {
				throw new RuntimeException("unknown database!!!");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean supportCreateIfNotExists(Dialect dialect) {
		return dialect instanceof MySQLDialect || dialect instanceof PostgreSQLDialect || dialect instanceof H2Dialect
				|| dialect instanceof SqliteDialect;
	}

	public static String columnType(Dialect dialect, Class<?> type, KdbColumn kdbColumn) {
		if (kdbColumn != null && kdbColumn.identity() && dialect instanceof SqliteDialect) {
			return KdbUtils.columnType(dialect, SqlTypes.INTEGER);
		} else if (type == int.class || type == Integer.class) {
			return KdbUtils.columnType(dialect, SqlTypes.INTEGER);
		} else if (type == byte.class || type == Byte.class) {
			return KdbUtils.columnType(dialect, SqlTypes.TINYINT);
		} else if (type == short.class || type == Short.class) {
			return KdbUtils.columnType(dialect, SqlTypes.SMALLINT);
		} else if (type == long.class || type == Long.class) {
			return KdbUtils.columnType(dialect, SqlTypes.BIGINT);
		} else if (type == float.class || type == Float.class) {
			return KdbUtils.columnType(dialect, SqlTypes.FLOAT);
		} else if (type == double.class || type == Double.class || type == BigDecimal.class) {
			return KdbUtils.columnType(dialect, SqlTypes.DECIMAL)
					.replace("$p", convert(kdbColumn, KdbColumn::precision, 12) + "")
					.replace("$s", convert(kdbColumn, KdbColumn::scope, 2) + "");
		} else if (type == boolean.class || type == Boolean.class) {
			return KdbUtils.columnType(dialect, SqlTypes.BOOLEAN);
		} else if (type == String.class) {
			return KdbUtils.columnType(dialect, SqlTypes.VARCHAR).replace("$l",
					convert(kdbColumn, KdbColumn::length, 60) + "");
		} else if (type.isEnum()) {
			return KdbUtils.columnType(dialect, SqlTypes.VARCHAR).replace("$l", "30");
		} else if (type == Timestamp.class) {
			return KdbUtils.columnType(dialect, SqlTypes.TIMESTAMP);
		} else if (java.util.Date.class.isAssignableFrom(type)) {
			return KdbUtils.columnType(dialect, SqlTypes.DATE);
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

	private static <T> T convert(KdbColumn kdbColumn, Function<KdbColumn, T> func, T def) {
		return Optional.ofNullable(kdbColumn).map(func).orElse(def);
	}

}
