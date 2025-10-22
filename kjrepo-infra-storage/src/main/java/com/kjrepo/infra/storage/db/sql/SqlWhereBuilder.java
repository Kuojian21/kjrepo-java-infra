package com.kjrepo.infra.storage.db.sql;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.storage.db.model.KdbModel;
import com.kjrepo.infra.storage.db.model.KdbProperty;

public class SqlWhereBuilder {

	private static final Set<String> SUPPORT_OPTS = Sets.newHashSet(">", ">=", "<", "<=", "=", "!=", "<>", "in",
			"not in");

	private KdbModel kdbModel;
	private final String logicalOpt;
	private final List<String> exprs = Lists.newArrayList();
	private final Map<String, Object> valueMap = Maps.newHashMap();
	private final List<Runnable> exprsFunc = Lists.newArrayList();
	private final LazySupplier<String> sql;

	public SqlWhereBuilder(KdbModel kdbModel, String logicalOpt) {
		this.kdbModel = kdbModel;
		this.logicalOpt = logicalOpt;
		this.sql = LazySupplier.wrap(() -> {
			Stream.of(exprsFunc).forEach(Runnable::run);
			return StringUtils.join(exprs, " " + this.logicalOpt + " ");
		});
	}

	public static SqlWhereBuilder and() {
		return builder("and");
	}

	public static SqlWhereBuilder or() {
		return builder("or");
	}

	public static SqlWhereBuilder builder(String logicalOpt) {
		return new SqlWhereBuilder(null, logicalOpt);
	}

	public SqlWhereBuilder expr(Map<String, Object> params) {
		check();
		params.forEach((name, value) -> {
			expr(name, "=", value);
		});
		return this;
	}

	public SqlWhereBuilder expr(SqlWhereBuilder sqlWhereBuilder) {
		check();
		sqlWhereBuilder.model(this.model());
		this.exprsFunc.add(() -> {
			if (StringUtils.isNotEmpty(sqlWhereBuilder.sql())) {
				this.exprs.add("(" + sqlWhereBuilder.sql() + ")");
				this.valueMap.putAll(sqlWhereBuilder.valueMap());
			}
		});
		return this;
	}

	public SqlWhereBuilder expr(String name, String compareOpt, Object value) {
		check();
		final Object vObj = Optional.of(value).map(v -> v.getClass().isArray() ? Lists.newArrayList((Object[]) v) : v)
				.get();
		final String opt = Optional
				.of(StringUtils.join(Stream.of(compareOpt.trim().split("\\s+")).filter(StringUtils::isNotEmpty)
						.map(String::trim).filter(StringUtils::isNotEmpty).toList(), " ").toLowerCase())
				.map(o -> {
					if (vObj instanceof Collection<?>) {
						switch (o) {
						case "=":
							return "in";
						case "!=":
						case "<>":
							return "not in";
						case "in":
						case "not in":
							return o;
						default:
							throw new RuntimeException("invalid expr:" + name + " " + compareOpt + " " + vObj);
						}
					} else {
						return o;
					}
				}).get();
		if (!SUPPORT_OPTS.contains(opt)) {
			throw new RuntimeException("Unsupported opt:" + compareOpt);
		}
		this.exprsFunc.add(() -> {
			final KdbProperty kdbProperty = model().getProperty(name);
			final String column = Optional.ofNullable(kdbProperty).map(KdbProperty::column).orElse(name);
			if (vObj instanceof Collection<?>) {
				this.exprs.add(column + " " + opt + "(" + StringUtils.join(Stream.of((Collection<?>) vObj).map(v -> {
					String var = SqlUtils.var();
					this.valueMap.put(var, Optional.ofNullable(kdbProperty).map(p -> p.cast(v)).orElse(v));
					return ":" + var;
				}).toList(), ",") + ")");
			} else {
				String var = SqlUtils.var();
				this.exprs.add(column + " " + opt + " :" + var);
				this.valueMap.put(var, Optional.ofNullable(kdbProperty).map(p -> p.cast(vObj)).orElse(vObj));
			}
		});
		return this;
	}

	public SqlWhereBuilder expr(String expr) {
		check();
		this.exprsFunc.add(() -> {
			this.exprs.add(expr);
		});
		return this;
	}

	public String sql() {
		return this.sql.get();
	}

	public Map<String, Object> valueMap() {
		this.sql.get();
		return this.valueMap;
	}

	public void model(KdbModel kdbModel) {
		if (!Objects.equals(kdbModel, this.kdbModel)) {
			check();
		}
		this.kdbModel = kdbModel;
	}

	public KdbModel model() {
		return this.kdbModel;
	}

	private void check() {
		if (this.sql.isInited()) {
			throw new RuntimeException("The method-sql() has already bean invoked!!!");
		}
	}

	public static void main(String[] args) {
		System.out.println(Lists.newArrayList("".split("\\s+")));
		System.out.println(Lists.newArrayList("=".split("\\s+")));
		System.out.println(Lists.newArrayList(">=".split("\\s+")));
		System.out.println(Lists.newArrayList("not in".split("\\s+")));
	}

}
