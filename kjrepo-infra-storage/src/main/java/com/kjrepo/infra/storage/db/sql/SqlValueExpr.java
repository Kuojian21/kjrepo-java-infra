package com.kjrepo.infra.storage.db.sql;

import java.util.Map;

import com.google.common.collect.Maps;

public class SqlValueExpr {

	public static final String INSERT_VALUE_PREFIX = "$INSERT_VALUE{";
	public static final String INSERT_VALUE_SUFFIX = "}";

	public static String sqlValueExprInsert(String column) {
		return SqlValueExpr.INSERT_VALUE_PREFIX + column + SqlValueExpr.INSERT_VALUE_SUFFIX;
	}

	public static SqlValueExpr of(String expr) {
		return of(expr, Maps.newHashMap());
	}

	public static SqlValueExpr of(String expr, Map<String, Object> valueMap) {
		return new SqlValueExpr(expr, valueMap);
	}

	private final String expr;
	private final Map<String, Object> valueMap;

	private SqlValueExpr(String expr, Map<String, Object> valueMap) {
		super();
		this.expr = expr;
		this.valueMap = valueMap;
	}

	public String expr() {
		return this.expr;
	}

	public Map<String, Object> valueMap() {
		return this.valueMap;
	}

}
