package com.kjrepo.infra.storage.db.sql;

import java.util.Map;

import com.google.common.collect.Maps;
import com.kjrepo.infra.storage.db.model.KdbDialect;
import com.kjrepo.infra.storage.db.model.KdbModel;

public abstract class SqlBuilder {

	private final Map<String, Object> valueMap = Maps.newHashMap();
	private String table;
	private KdbModel kdbModel;
	private KdbDialect kdbDialect;

	public static SqlInsertBuilder insert() {
		return new SqlInsertBuilder();
	}

	public static SqlInsertBuilder insert(boolean ignore) {
		return new SqlInsertBuilder(ignore);
	}

	public static SqlUpsertBuilder upsert(Map<String, Object> update) {
		return new SqlUpsertBuilder(update);
	}

	public static SqlSelectBuilder select() {
		return new SqlSelectBuilder();
	}

	public static SqlUpdateBuilder update(Map<String, Object> update) {
		return new SqlUpdateBuilder(update);
	}

	public static SqlDeleteBuilder delete() {
		return new SqlDeleteBuilder();
	}

	public SqlBuilder init(String table, KdbModel kdbModel, KdbDialect kdbDialect) {
		this.table = table;
		this.kdbModel = kdbModel;
		this.kdbDialect = kdbDialect;
		return this;
	}

	public final SqlBuilder valueMap(Map<String, Object> valueMap) {
		this.valueMap.putAll(valueMap);
		return this;
	}

	public KdbModel model() {
		return this.kdbModel;
	}

	public KdbDialect dialect() {
		return this.kdbDialect;
	}

	public String table() {
		return this.table;
	}

	public abstract String sql();

	public final Map<String, Object> valueMap() {
		return this.valueMap;
	}

}
