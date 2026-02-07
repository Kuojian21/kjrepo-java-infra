package com.kjrepo.infra.storage.db.sql;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kjrepo.infra.common.info.Pair;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.storage.db.model.KdbProperty;

public class SqlUpdateBuilder extends SqlBuilder {

	private final LazySupplier<String> sql;
	private SqlWhereBuilder where;

	public SqlUpdateBuilder(Map<String, Object> update) {
		this(Stream.of(update).map(e -> Pair.pair(e.getKey(), e.getValue())).toList());
	}

	public SqlUpdateBuilder(List<Pair<String, Object>> update) {
		this.sql = LazySupplier.wrap(() -> {
			StringBuilder sql = new StringBuilder().append("update ").append(table()).append(" set ")
					.append(StringUtils.join(Stream.of(update).map(e -> {
						KdbProperty property = model().getProperty(e.getKey());
						if (e.getValue() instanceof SqlValueExpr) {
							SqlValueExpr svExpr = (SqlValueExpr) e.getValue();
							return property.column() + " = " + StringSubstitutor.replace(svExpr.expr(),
									Stream.of(svExpr.valueMap()).collect(Collectors.toMap(Map.Entry::getKey, en -> {
										String var = SqlUtils.var();
										this.valueMap().put(var, en.getValue());
										return ":" + var;
									})), SqlValueExpr.INSERT_VALUE_PREFIX, SqlValueExpr.INSERT_VALUE_SUFFIX);
						}
						String var = SqlUtils.var();
						valueMap().put(var, property.cast(e.getValue()));
						return property.column() + " = :" + var;
					}).toList(), ","));
			if (this.where != null && StringUtils.isNotEmpty(this.where.init(table(), model(), dialect()).sql())) {
				sql.append(" where ").append(this.where.sql());
				this.valueMap().putAll(this.where.valueMap());
			}
			return sql.toString();
		});
	}

	public SqlUpdateBuilder where(SqlWhereBuilder sqlWhereBuilder) {
		this.where = sqlWhereBuilder;
		return this;
	}

	@Override
	public String sql() {
		return this.sql.get();
	}

}
