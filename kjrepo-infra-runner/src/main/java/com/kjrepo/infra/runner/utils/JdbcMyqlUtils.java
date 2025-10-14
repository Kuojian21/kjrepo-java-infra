package com.kjrepo.infra.runner.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.text.StringSubstitutor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class JdbcMyqlUtils {

	public static List<String> columns(String host, int port, String database, String username, String password,
			String table) throws SQLException, ClassNotFoundException {
		List<String> columns = Lists.newArrayList();
		Class.forName("com.mysql.cj.jdbc.Driver");
		try (Connection conn = DriverManager.getConnection(
				StringSubstitutor.replace("jdbc:mysql://${host}:${port}/${database}?useUnicode=true" //
						+ "&autoReconnectForPools=true" //
						+ "&useCompression=true" //
						+ "&rewriteBatchedStatements=true" //
						+ "&useConfigs=maxPerformance" //
						+ "&useSSL=false" //
						+ "&useAffectedRows=true" //
						+ "&allowMultiQueries=true", ImmutableMap.of("host", host, "port", port, "database", database)),
				username, password)) {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("show full columns from " + table);
			while (rs.next()) {
				columns.add(rs.getString("Field"));
			}
		}
		return columns;
	}

}
