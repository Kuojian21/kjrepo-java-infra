package com.kjrepo.infra.storage.db.sql;

import java.util.Map;

import com.kjrepo.infra.storage.db.model.KdbModel;

public abstract class SqlBuilder {

	private final KdbModel kdbModel;

	protected SqlBuilder(KdbModel kdbModel) {
		this.kdbModel = kdbModel;
	}

	public static SqlInsertBuilder insert(KdbModel kdbModel, String table) {
		return new SqlInsertBuilder(kdbModel, table);
	}

	public static SqlSelectBuilder select(KdbModel kdbModel, String table) {
		return new SqlSelectBuilder(kdbModel, table);
	}

	public static SqlUpdateBuilder update(KdbModel kdbModel, String table, Map<String, Object> update) {
		return new SqlUpdateBuilder(kdbModel, table, update);
	}

	public static SqlDeleteBuilder delete(KdbModel kdbModel, String table) {
		return new SqlDeleteBuilder(kdbModel, table);
	}

	public KdbModel model() {
		return this.kdbModel;
	}

	public abstract String sql();

	public abstract Map<String, Object> valueMap();

}
