package com.kjrepo.infra.storage.db.sql;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Stream;
import com.google.common.collect.Lists;
import com.kjrepo.infra.storage.db.model.Model;
import com.kjrepo.infra.storage.db.utils.SqlUtils;

public class SqlInsertBuilder extends SqlBuilder {

	private List<String> insertValues = Lists.newArrayList();

	public SqlInsertBuilder(Model model, String table) {
		super(model);
		super.sql.append("insert into");
		super.sql.append(" ").append(table).append("(");
		super.sql.append(StringUtils
				.join(Stream.of(model.getPropertyList()).filter(p -> !p.auto()).map(p -> p.column()).toList(), " , "));
		super.sql.append(")");
	}

	public <T> SqlInsertBuilder model(T[] models) {
		Stream.of(models).forEach(model -> model(model));
		return this;
	}

	public <T> SqlInsertBuilder model(List<T> models) {
		models.forEach(model -> model(model));
		return this;
	}

	public <T> SqlInsertBuilder model(T model) {
		if (model == null) {
			return this;
		}
		List<String> list = Lists.newArrayList();
		Stream.of(super.model.getPropertyList()).filter(p -> !p.auto()).forEach(p -> {
			String var = SqlUtils.var();
			list.add(":" + var);
			super.valueMap.put(var, p.readAndCast(model));
		});
		this.insertValues
				.add(new StringBuilder().append("(").append(StringUtils.join(list, ",")).append(")").toString());
		return this;
	}

	public String sql() {
		StringBuilder builder = new StringBuilder();
		builder.append(sql).append(" values").append(StringUtils.join(this.insertValues, ","));
		return builder.toString();
	}

	public Map<String, Object> valueMap() {
		return super.valueMap;
	}

}
