package com.kjrepo.infra.storage.db.sql;

public class SqlExprValue {

	public static SqlExprValue of(String expr) {
		return new SqlExprValue(expr);
	}

	private final String expr;

	private SqlExprValue(String expr) {
		super();
		this.expr = expr;
	}

	public String expr() {
		return this.expr;
	}

}
