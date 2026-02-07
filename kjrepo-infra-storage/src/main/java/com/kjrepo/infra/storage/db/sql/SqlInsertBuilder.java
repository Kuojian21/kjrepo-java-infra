package com.kjrepo.infra.storage.db.sql;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Stream;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.lazy.LazySupplier;

public class SqlInsertBuilder extends SqlBuilder {

	private final List<String> exprs = Lists.newArrayList();
	private final List<Runnable> exprsFunc = Lists.newArrayList();
	private final LazySupplier<String> sql;

	public SqlInsertBuilder() {
		this(false);
	}

	public SqlInsertBuilder(boolean ignore) {
		this.sql = LazySupplier.wrap(() -> {
			Stream.of(exprsFunc).forEach(Runnable::run);
			StringBuilder sql = new StringBuilder();
			if (ignore) {
				switch (dialect()) {
				case MySQL:
					return sql.append("insert ignore into").append(" ").append(table()).append("(")
							.append(StringUtils.join(Stream.of(model().properties()).filter(p -> !p.identity())
									.map(p -> p.column()).toList(), " , "))
							.append(")").append(" values").append(StringUtils.join(this.exprs, ",")).toString();
				case PostgreSQL:
					return sql.append("insert into").append(" ").append(table()).append("(")
							.append(StringUtils.join(Stream.of(model().properties()).filter(p -> !p.identity())
									.map(p -> p.column()).toList(), " , "))
							.append(")").append(" values").append(StringUtils.join(this.exprs, ","))
							.append(" ON CONFLICT DO NOTHING").toString();
				case H2:
				case Sqlite:
				case Oracle:
				case SQLServer:
				default:

				}
			}
			return sql.append("insert into").append(" ").append(table()).append("(")
					.append(StringUtils.join(
							Stream.of(model().properties()).filter(p -> !p.identity()).map(p -> p.column()).toList(),
							" , "))
					.append(")").append(" values").append(StringUtils.join(this.exprs, ",")).toString();
		});
	}

	public <T> SqlInsertBuilder model(T[] models) {
		check();
		Stream.of(models).forEach(model -> model(model));
		return this;
	}

	public <T> SqlInsertBuilder model(List<T> models) {
		check();
		models.forEach(model -> model(model));
		return this;
	}

	public <T> SqlInsertBuilder model(T model) {
		check();
		if (model == null) {
			return this;
		}
		exprsFunc.add(() -> {
			List<String> list = Lists.newArrayList();
			Stream.of(model().properties()).filter(p -> !p.identity()).forEach(p -> {
				String var = SqlUtils.var();
				list.add(":" + var);
				valueMap().put(var, p.readAndCast(model));
			});
			exprs.add(new StringBuilder().append("(").append(StringUtils.join(list, ",")).append(")").toString());
		});
		return this;
	}

	@Override
	public String sql() {
		return sql.get();
	}

	private void check() {
		if (this.sql.isInited()) {
			throw new RuntimeException("The method-sql() has already bean invoked!!!");
		}
	}

}
