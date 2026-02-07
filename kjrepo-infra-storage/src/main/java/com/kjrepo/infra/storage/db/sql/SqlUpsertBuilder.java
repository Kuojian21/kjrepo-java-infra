package com.kjrepo.infra.storage.db.sql;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Stream;
import com.kjrepo.infra.common.info.Pair;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.storage.db.model.KdbProperty;

public class SqlUpsertBuilder extends SqlInsertBuilder {

	private final LazySupplier<String> sql;

	public SqlUpsertBuilder(Map<String, Object> update) {
		this(Stream.of(update).map(e -> Pair.pair(e.getKey(), e.getValue())).toList());
	}

	public SqlUpsertBuilder(List<Pair<String, Object>> update) {
		this.sql = LazySupplier.wrap(() -> {
			StringBuilder sql = new StringBuilder().append(super.sql());
			switch (dialect()) {
			case MySQL:
				return sql.append(" ON DUPLICATE KEY UPDATE ").append(StringUtils.join(Stream.of(update).map(e -> {
					KdbProperty property = model().getProperty(e.getKey());
					if (e.getValue() instanceof SqlValueExpr) {
						return property.column() + " = " + ((SqlValueExpr) e.getValue()).expr();
					}
					String var = SqlUtils.var();
					valueMap().put(var, property.cast(e.getValue()));
					return property.column() + " = :" + var;
				}).toList(), ",")).toString();
			case PostgreSQL:
				return sql.append(" ON CONFLICT DO UPDATE SET ").append(StringUtils.join(Stream.of(update).map(e -> {
					KdbProperty property = model().getProperty(e.getKey());
					if (e.getValue() instanceof SqlValueExpr) {
						return property.column() + " = " + ((SqlValueExpr) e.getValue()).expr();
					}
					String var = SqlUtils.var();
					valueMap().put(var, property.cast(e.getValue()));
					return property.column() + " = :" + var;
				}).toList(), ",")).toString();
			case H2:
			case Sqlite:
			case Oracle:
			case SQLServer:
			default:

			}
			return sql.toString();
		});
	}

	@Override
	public String sql() {
		return sql.get();
	}

}
