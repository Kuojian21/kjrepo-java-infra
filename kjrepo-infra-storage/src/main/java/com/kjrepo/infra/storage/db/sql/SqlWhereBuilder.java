package com.kjrepo.infra.storage.db.sql;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.function.Consumer;
import com.google.common.collect.Lists;
import com.kjrepo.infra.storage.db.model.Model;
import com.kjrepo.infra.storage.db.model.Property;
import com.kjrepo.infra.storage.db.utils.SqlUtils;

public class SqlWhereBuilder extends SqlBuilder {

	private final String logicalOpt;
	private final List<String> exprs = Lists.newArrayList();

	public SqlWhereBuilder(Model model, String logicalOpt) {
		super(model);
		this.logicalOpt = logicalOpt;
	}

	public static SqlWhereBuilder and(Model model) {
		return builder(model, "and");
	}

	public static SqlWhereBuilder or(Model model) {
		return builder(model, "or");
	}

	public static SqlWhereBuilder builder(Model model) {
		return and(model);
	}

	public static SqlWhereBuilder builder(Model model, String logicalOpt) {
		return new SqlWhereBuilder(model, logicalOpt);
	}

	public SqlWhereBuilder expr(Map<String, Object> params) {
		params.forEach((name, value) -> {
			expr(name, "=", value);
		});
		return this;
	}

	public SqlWhereBuilder andExpr(Consumer<SqlWhereBuilder> consumer) {
		SqlWhereBuilder builder = SqlWhereBuilder.and(model);
		consumer.accept(builder);
		expr(builder);
		return this;
	}

	public SqlWhereBuilder orExpr(Consumer<SqlWhereBuilder> consumer) {
		SqlWhereBuilder builder = SqlWhereBuilder.or(model);
		consumer.accept(builder);
		expr(builder);
		return this;
	}

	public SqlWhereBuilder expr(SqlWhereBuilder sqlWhereBuilder) {
		if (StringUtils.isNotEmpty(sqlWhereBuilder.sql())) {
			this.exprs.add("(" + sqlWhereBuilder.sql() + ")");
			this.valueMap.putAll(sqlWhereBuilder.valueMap());
		}
		return this;
	}

	public SqlWhereBuilder expr(String name, String compareOpt, Object value) {
		String var = SqlUtils.var();
		Property property = super.model.getProperty(name);
		if (property == null) {
			this.exprs.add(name + " " + compareOpt + " :" + var);
			super.valueMap.put(var, value);
		} else {
			this.exprs.add(property.column() + " " + compareOpt + " :" + var);
			super.valueMap.put(var, property.cast(value));
		}
		return this;
	}

	public SqlWhereBuilder expr(String expr) {
		this.exprs.add(expr);
		return this;
	}

	@Override
	public String sql() {
		return StringUtils.join(exprs, " " + logicalOpt + " ");
	}

	@Override
	public Map<String, Object> valueMap() {
		return super.valueMap;
	}

}
