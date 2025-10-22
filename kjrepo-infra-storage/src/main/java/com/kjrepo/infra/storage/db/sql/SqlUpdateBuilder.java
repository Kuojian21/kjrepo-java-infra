package com.kjrepo.infra.storage.db.sql;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.info.Pair;
import com.kjrepo.infra.storage.db.model.KdbModel;
import com.kjrepo.infra.storage.db.model.KdbProperty;

public class SqlUpdateBuilder extends SqlBuilder {

	private final Map<String, Object> valueMap = Maps.newHashMap();
	private final String sql;
	private SqlWhereBuilder sqlWhereBuilder;

	public SqlUpdateBuilder(KdbModel kdbModel, String table, Map<String, Object> update) {
		this(kdbModel, table, Stream.of(update).map(e -> Pair.pair(e.getKey(), e.getValue())).toList());
	}

	public SqlUpdateBuilder(KdbModel kdbModel, String table, List<Pair<String, Object>> update) {
		super(kdbModel);
		this.sql = new StringBuilder().append("update ").append(table).append(" set ")
				.append(StringUtils.join(Stream.of(update).map(e -> {
					KdbProperty property = kdbModel.getProperty(e.getKey());
					if (e.getValue() instanceof SqlExprValue) {
						return property.column() + " = " + ((SqlExprValue) e.getValue()).expr();
					}
					String var = SqlUtils.var();
					valueMap.put(var, property.cast(e.getValue()));
					return property.column() + " = :" + var;
				}).toList(), ",")).toString();
	}

	public SqlUpdateBuilder where(SqlWhereBuilder sqlWhereBuilder) {
		this.sqlWhereBuilder = sqlWhereBuilder;
		this.sqlWhereBuilder.model(model());
		return this;
	}

	@Override
	public String sql() {
		return new StringBuilder().append(this.sql).append(Optional.ofNullable(this.sqlWhereBuilder)
				.map(SqlWhereBuilder::sql).filter(StringUtils::isNotEmpty).map(w -> " where " + w).orElse(""))
				.toString();
	}

	@Override
	public Map<String, Object> valueMap() {
		Map<String, Object> map = Maps.newHashMap();
		map.putAll(this.valueMap);
		map.putAll(
				Optional.ofNullable(this.sqlWhereBuilder).map(SqlWhereBuilder::valueMap).orElseGet(Maps::newHashMap));
		return map;
	}

}
