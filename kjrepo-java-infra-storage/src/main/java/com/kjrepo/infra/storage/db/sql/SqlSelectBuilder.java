package com.kjrepo.infra.storage.db.sql;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Supplier;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.storage.db.model.KdbProperty;

public class SqlSelectBuilder extends SqlBuilder {

	private final LazySupplier<String> sql;
	private SqlWhereBuilder where;
	private List<Supplier<String>> groupExprs = Lists.newArrayList();
	private List<Supplier<String>> orderExprs = Lists.newArrayList();
	private SqlWhereBuilder having;
	private int limit;
	private int offset;

	public SqlSelectBuilder() {
		this.sql = sql(() -> new StringBuilder().append("select * from ").append(table()).toString());
	}

	public SqlSelectBuilder(List<String> columns) {
		this.sql = sql(() -> new StringBuilder().append("select ")
				.append(StringUtils.join(Stream.of(columns)
						.map(c -> Optional.ofNullable(model().getProperty(c)).map(KdbProperty::column).orElse(c))
						.toList(), ","))
				.append(" from ").append(table()).toString());
	}

	public SqlSelectBuilder where(SqlWhereBuilder where) {
		this.where = where;
		return this;
	}

	public SqlSelectBuilder group(String... columns) {
		Stream.ofNullable(columns).map(c -> (Supplier<String>) () -> Optional.ofNullable(model().getProperty(c))
				.map(KdbProperty::column).orElse(c)).forEach(groupExprs::add);
		return this;
	}

	public SqlSelectBuilder having(SqlWhereBuilder having) {
		this.having = having;
		return this;
	}

	public SqlSelectBuilder asc(String... columns) {
		Stream.ofNullable(columns).map(c -> (Supplier<String>) () -> Optional.ofNullable(model().getProperty(c))
				.map(KdbProperty::column).orElse(c) + " asc").forEach(orderExprs::add);
		return this;
	}

	public SqlSelectBuilder desc(String... columns) {
		Stream.ofNullable(columns).map(c -> (Supplier<String>) () -> Optional.ofNullable(model().getProperty(c))
				.map(KdbProperty::column).orElse(c) + " desc").forEach(orderExprs::add);
		return this;
	}

	public SqlSelectBuilder limit(int limit) {
		return this.limit(limit, 0);
	}

	public SqlSelectBuilder limit(int limit, int offset) {
		this.limit = limit;
		this.offset = offset;
		return this;
	}

	private LazySupplier<String> sql(Supplier<String> select) {
		return LazySupplier.wrap(() -> {
			StringBuilder sql = new StringBuilder().append(select.get());
			if (this.where != null && StringUtils.isNotEmpty(this.where.init(table(), model(), dialect()).sql())) {
				sql.append(" where ").append(this.where.sql());
				this.valueMap().putAll(this.where.valueMap());
			}
			if (this.groupExprs.size() > 0) {
				sql.append(" group by ")
						.append(StringUtils.join(Stream.of(this.groupExprs).map(Supplier::get).toList(), ","));
				if (this.having != null
						&& StringUtils.isNotEmpty(this.having.init(table(), model(), dialect()).sql())) {
					sql.append(" having ").append(having.sql());
					this.valueMap().putAll(this.having.valueMap());
				}
			}
			if (this.orderExprs.size() > 0) {
				sql.append(" order by ")
						.append(StringUtils.join(Stream.of(this.orderExprs).map(Supplier::get).toList(), ","));
			}
			if (limit <= 0) {
				return sql.toString();
			} else {
				switch (dialect()) {
				case H2:
				case Sqlite:
				case PostgreSQL:
					sql.append(" limit " + this.limit);
					if (this.offset > 0) {
						sql.append(" offset " + this.offset);
					}
					return sql.toString();
				case MySQL:
					if (this.offset > 0) {
						sql.append(" limit " + this.offset + "," + this.limit);
					} else {
						sql.append(" limit " + this.limit);
					}
					return sql.toString();
				case SQLServer:
					return sql.toString().replaceFirst("^select", "select top " + this.limit);
				case Oracle:
					return "select * from (select rownum as rn,t.* from (" + sql.toString()
							+ ") as t) as tt where tt.rn > " + this.offset + " and tt.rn <= "
							+ (this.offset + this.limit);
				default:
					return sql.toString();
				}
			}
		});
	}

	@Override
	public String sql() {
		return this.sql.get();
	}

}
