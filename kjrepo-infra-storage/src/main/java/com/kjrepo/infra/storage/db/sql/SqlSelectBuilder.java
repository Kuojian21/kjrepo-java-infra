package com.kjrepo.infra.storage.db.sql;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.kjrepo.infra.storage.db.model.Model;

public class SqlSelectBuilder extends SqlBuilder {

	private SqlWhereBuilder sqlWhereBuilder;

	public SqlSelectBuilder(Model model, String table) {
		super(model);
		super.sql.append("select * from ").append(table);
	}

	public SqlSelectBuilder(Model model, String table, List<String> columns) {
		super(model);
		super.sql.append("select ").append(StringUtils.join(columns, ",")).append(" from ").append(table);
	}

	public SqlSelectBuilder where(SqlWhereBuilder sqlWhereBuilder) {
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
