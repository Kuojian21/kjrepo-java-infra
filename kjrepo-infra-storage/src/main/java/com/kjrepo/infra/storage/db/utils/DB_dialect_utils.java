package com.kjrepo.infra.storage.db.utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.storage.db.model.KdbDialect;

public class DB_dialect_utils {

	private static final Logger logger = LoggerUtils.logger(DB_dialect_utils.class);

	public static KdbDialect dialect(DataSource datasource) {
		try (Connection conn = datasource.getConnection()) {
			return dialect(conn);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static KdbDialect dialect(NamedParameterJdbcOperations jdbcTemplate) {
		return jdbcTemplate.getJdbcOperations().execute(new ConnectionCallback<KdbDialect>() {

			@Override
			public KdbDialect doInConnection(Connection con) throws SQLException, DataAccessException {
				return dialect(con);
			}

		});
	}

	public static KdbDialect dialect(Connection conn) {
		try {
			String dname = conn.getMetaData().getDatabaseProductName();
			String dversion = conn.getMetaData().getDatabaseProductVersion();
			logger.info("database:{} version:{}", dname, dversion);
			return KdbDialect.from(dname, dversion);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
