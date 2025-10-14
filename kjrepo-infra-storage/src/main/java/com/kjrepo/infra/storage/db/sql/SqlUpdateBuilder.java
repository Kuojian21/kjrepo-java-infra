package com.kjrepo.infra.storage.db.sql;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.storage.db.model.Model;
import com.kjrepo.infra.storage.db.model.Property;
import com.kjrepo.infra.storage.db.utils.SqlUtils;

public class SqlUpdateBuilder extends SqlBuilder {

	private List<String> updateSets = Lists.newArrayList();
	private SqlWhereBuilder sqlWhereBuilder;

	public SqlUpdateBuilder(Model model, String table) {
		super(model);
		super.sql.append("update ").append(table).append(" set");
	}

	public SqlUpdateBuilder set(Map<String, Object> updateSetMap) {
		updateSetMap.forEach((c, v) -> set(c, v));
		return this;
	}

	public SqlUpdateBuilder set(String column, Object value) {
		String var = SqlUtils.var();
		Property property = super.model.getProperty(column);
		this.updateSets.add(property.column() + " = :" + var);
		super.valueMap.put(var, property.cast(value));
		return this;
	}

	public SqlUpdateBuilder where(SqlWhereBuilder sqlWhereBuilder) {
		this.sqlWhereBuilder = sqlWhereBuilder;
		return this;
	}

	@Override
	public String sql() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.sql).append(" ").append(StringUtils.join(this.updateSets, ","));
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
