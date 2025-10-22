package com.kjrepo.infra.storage.db.sql;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.common.collect.Maps;
import com.kjrepo.infra.storage.db.model.KdbModel;
import com.kjrepo.infra.storage.db.model.KdbProperty;

public class SqlSelectBuilder extends SqlBuilder {

	private final String sql;
	private SqlWhereBuilder sqlWhereBuilder;

	public SqlSelectBuilder(KdbModel kdbModel, String table) {
		super(kdbModel);
		this.sql = new StringBuilder().append("select * from ").append(table).toString();
	}

	public SqlSelectBuilder(KdbModel kdbModel, String table, List<String> columns) {
		super(kdbModel);
		this.sql = new StringBuilder().append("select ")
				.append(StringUtils.join(Stream.of(columns)
						.map(c -> Optional.ofNullable(kdbModel.getProperty(c)).map(KdbProperty::column).orElse(c))
						.toList(), ","))
				.append(" from ").append(table).toString();
	}

	public SqlSelectBuilder where(SqlWhereBuilder sqlWhereBuilder) {
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
