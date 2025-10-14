package com.kjrepo.infra.storage.db.sql;

import java.util.Map;

import com.google.common.collect.Maps;
import com.kjrepo.infra.storage.db.model.Model;

public abstract class SqlBuilder {

	protected final Model model;
	protected final StringBuilder sql = new StringBuilder();
	protected final Map<String, Object> valueMap = Maps.newHashMap();

	protected SqlBuilder(Model model) {
		this.model = model;
	}

	public static SqlInsertBuilder insert(Model model, String table) {
		return new SqlInsertBuilder(model, table);
	}

	public static SqlSelectBuilder select(Model model, String table) {
		return new SqlSelectBuilder(model, table);
	}

	public static SqlUpdateBuilder update(Model model, String table) {
		return new SqlUpdateBuilder(model, table);
	}

	public static SqlDeleteBuilder delete(Model model, String table) {
		return new SqlDeleteBuilder(model, table);
	}

	public abstract String sql();

	public abstract Map<String, Object> valueMap();

}
