package com.kjrepo.infra.storage.db.sql;

import org.apache.commons.lang3.StringUtils;

import com.kjrepo.infra.common.lazy.LazySupplier;

public class SqlDeleteBuilder extends SqlBuilder {

	private final LazySupplier<String> sql;
	private SqlWhereBuilder where;

	public SqlDeleteBuilder() {
		this.sql = LazySupplier.wrap(() -> {
			StringBuilder sql = new StringBuilder().append("delete from " + table());
			if (this.where != null && StringUtils.isNotEmpty(this.where.init(table(), model(), dialect()).sql())) {
				sql.append(" where ").append(this.where.sql());
				this.valueMap().putAll(this.where.valueMap());
			}
			return sql.toString();
		});
	}

	public SqlDeleteBuilder where(SqlWhereBuilder where) {
		this.where = where;
		return this;
	}

	@Override
	public String sql() {
		return this.sql.get();
	}

}
