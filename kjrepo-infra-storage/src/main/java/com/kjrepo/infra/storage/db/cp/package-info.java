package com.kjrepo.infra.storage.db.cp;

/*
 * com.zaxxer.hikari.HikariDataSource.HikariDataSource
 * 
 * com.alibaba.druid.util.DruidDataSourceUtils.configFromProperties()
 * com.alibaba.druid.pool.DruidDataSource
 * com.alibaba.druid.pool.DruidDataSourceFactory
 * 
 * org.apache.commons.dbcp2.BasicDataSource
 * org.apache.commons.dbcp2.BasicDataSourceFactory
 * org.apache.commons.dbcp2.managed.BasicManagedDataSource
 * org.apache.commons.dbcp2.datasources.PerUserPoolDataSource
 * org.apache.commons.dbcp2.datasources.PerUserPoolDataSourceFactory
 * org.apache.commons.dbcp2.datasources.SharedPoolDataSource
 * org.apache.commons.dbcp2.datasources.SharedPoolDataSourceFactory
 * 
 */

import java.util.concurrent.TimeUnit;
import com.zaxxer.hikari.HikariConfig;

class Hikari {
	public static void demo() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://${host}:${port}/${database}?useUnicode=true" //
				+ "&autoReconnectForPools=true" //
				+ "&useCompression=true" //
				+ "&rewriteBatchedStatements=true" //
				+ "&useConfigs=maxPerformance" //
				+ "&useSSL=false" //
				+ "&useAffectedRows=true" //
				+ "&allowMultiQueries=true");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setAutoCommit(true);
		config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(20L));
		config.setValidationTimeout(TimeUnit.SECONDS.toMillis(5L));
		config.setIdleTimeout(TimeUnit.MINUTES.toMillis(1L));
		config.setMaximumPoolSize(100);
		config.setMinimumIdle(0);
		config.setMaxLifetime(0L);
		config.setRegisterMbeans(false);
	}

}
