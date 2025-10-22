package com.kjrepo.infra.storage.db.sql;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Optional;
import com.google.common.collect.Maps;
import com.kjrepo.infra.storage.db.model.KdbModel;

public class SqlDeleteBuilder extends SqlBuilder {

	private final String sql;
	private SqlWhereBuilder sqlWhereBuilder;

	public SqlDeleteBuilder(KdbModel kdbModel, String table) {
		super(kdbModel);
		this.sql = "delete from " + table;
	}

	public SqlDeleteBuilder where(SqlWhereBuilder sqlWhereBuilder) {
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
		return Optional.ofNullable(this.sqlWhereBuilder).map(SqlWhereBuilder::valueMap).orElseGet(Maps::newHashMap);
	}

}
