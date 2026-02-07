package com.kjrepo.infra.storage.db.model;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.OracleDialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.SQLServerDialect;

import com.kjrepo.infra.storage.db.dialect.H2Dialect;
import com.kjrepo.infra.storage.db.dialect.SqliteDialect;

public enum KdbDialect {
	H2("H2", new H2Dialect()),
	Sqlite("SQLite", new SqliteDialect()),
	MySQL("MySQL", new MySQLDialect()),
	PostgreSQL("PostgreSQL", new PostgreSQLDialect()),
	SQLServer("SQL Server", new SQLServerDialect()),
	Oracle("Oracle", new OracleDialect());

	private final String mark;
	private final Dialect dialect;

	private KdbDialect(String mark, Dialect dialect) {
		this.mark = mark;
		this.dialect = dialect;
	}

	public Dialect dialect() {
		return this.dialect;
	}

	public static KdbDialect from(String databaseProductName, String databaseProductVersion) {
		for (KdbDialect e : KdbDialect.values()) {
			if (databaseProductName.contains(e.mark)) {
				return e;
			}
		}
		throw new RuntimeException("unknown database!!!");
	}

}
