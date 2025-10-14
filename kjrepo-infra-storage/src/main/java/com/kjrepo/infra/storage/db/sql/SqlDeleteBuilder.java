package com.kjrepo.infra.storage.db.sql;

import java.util.Map;

import com.google.common.collect.Maps;
import com.kjrepo.infra.storage.db.model.Model;

public class SqlDeleteBuilder extends SqlBuilder {

	private SqlWhereBuilder sqlWhereBuilder;

	public SqlDeleteBuilder(Model model, String table) {
		super(model);
		super.sql.append("delete from ").append(table);
	}

	public SqlDeleteBuilder where(SqlWhereBuilder sqlWhereBuilder) {
		this.sqlWhereBuilder = sqlWhereBuilder;
		return this;
	}

	@Override
	public String sql() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.sql);
		if (sqlWhereBuilder != null && sqlWhereBuilder.sql().length() > 0) {
			builder.append(" where ").append(sqlWhereBuilder.sql());
		}
		return builder.toString();
	}

	@Override
	public Map<String, Object> valueMap() {
		Map<String, Object> map = Maps.newHashMap();
		map.putAll(super.valueMap);
		map.putAll(sqlWhereBuilder.valueMap());
		return map;
	}

}
