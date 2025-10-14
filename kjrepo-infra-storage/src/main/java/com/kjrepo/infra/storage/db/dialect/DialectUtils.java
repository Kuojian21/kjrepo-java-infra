package com.kjrepo.infra.storage.db.dialect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.OracleDialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.slf4j.Logger;

import com.google.common.collect.Maps;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class DialectUtils {

	private static final Logger logger = LoggerUtils.logger(DialectUtils.class);

	public static Map<Class<?>, Method> methods = Maps.newConcurrentMap();

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
