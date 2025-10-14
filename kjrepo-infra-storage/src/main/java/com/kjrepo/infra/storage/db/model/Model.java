package com.kjrepo.infra.storage.db.model;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.dialect.Dialect;
import org.springframework.beans.BeanUtils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.base.CaseFormat;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.info.Pair;
import com.kjrepo.infra.text.freemarker.Freemarker;

public class Model {

	private static final LoadingCache<Class<?>, Model> repo = CacheBuilder.newBuilder().build(new CacheLoader<>() {
		@Override
		public Model load(Class<?> key) throws Exception {
			return new Model(key);
		}
	});

	public static Model of(Class<?> clazz) {
		return repo.getUnchecked(clazz);
	}

	public static Model of(String name, Ktable ktable) {
		return new Model(name, ktable);
	}

	private final String name;
	private final Ktable ktable;
	private final List<Property> propertyList;
	private final Map<String, Property> propertyMap;

	public Model(Class<?> clazz) {
		this.name = clazz.getSimpleName();
		this.ktable = clazz.getAnnotation(Ktable.class);
		Map<String, PropertyDescriptor> pdmap = descriptors(clazz, Maps.newHashMap());
		this.propertyList = Stream.of(fields(clazz, Lists.newArrayList()))
				.map(f -> Property.of(f, pdmap.get(f.getName()))).toList();
		this.propertyMap = Stream.of(this.propertyList)
				.flatMap(p -> Stream.of(Lists.newArrayList(Pair.pair(p.name(), p), Pair.pair(p.column(), p))))
				.distinct().collect(Collectors.toMap(Pair::getKey, Pair::getValue));
	}

	public Model(String name, Ktable ktable) {
		this.name = name;
		this.ktable = ktable;
		this.propertyList = Lists.newArrayList();
		this.propertyMap = Maps.newHashMap();
	}

	public String table() {
		if (ktable != null && StringUtils.isNotEmpty(ktable.name())) {
			return ktable.name();
		}
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.name);
	}

	public List<Property> getPropertyList() {
		return propertyList;
	}

	public Model addProperty(Property property) {
		this.propertyList.add(property);
		this.propertyMap.put(property.name(), property);
		this.propertyMap.put(property.column(), property);
		return this;
	}

	public Property getProperty(String name) {
		return this.propertyMap.get(name);
	}

	public String toCreateSql(Dialect dialect) {
		StringBuilder sql = new StringBuilder();
		sql.append(dialect.getCreateTableString() + " " + this.table() + "(\n\t");
		sql.append(StringUtils.join(Stream.of(this.propertyList).map(py -> py.toCreateSql(dialect)).toList(), ",\n\t"));

		if (ktable != null && ktable.indexes() != null && ktable.indexes().length > 0) {
			sql.append(",\n\t");
			sql.append(StringUtils.join(Stream.of(ktable.indexes()).map(index -> {
				if (index.primary()) {
					return "primary key(" + StringUtils.join(index.columns(), ",") + ")";
				}
				if (index.unique()) {
					return "unique " + index.name() + "(" + StringUtils.join(index.columns(), ",") + ")";
				}
				return "index " + index.name() + "(" + StringUtils.join(index.columns(), ",") + ")";
			}).toList(), ",\n\t"));
		}

		sql.append("\n);");
		return sql.toString();
	}

	public String toJavaCode(String pkg) {
		Map<String, Object> data = Maps.newHashMap();
		data.put("pkg", pkg);
		data.put("imports", Stream.of(this.propertyList)
				.filter(p -> !p.type().isPrimitive() && !p.type().getName().startsWith("java.lang.")).toList());
		data.put("name", this.name);
		data.put("fields", Stream.of(this.propertyList).map(p -> ImmutableMap.of("name", p.name(), "type",
				p.type().isPrimitive() ? p.type().toString() : p.type().getSimpleName())).toList());
		return Freemarker.freemarker("ftl").process("kjrepo-repository-java-model-code.ftl", data);
	}

	private Map<String, PropertyDescriptor> descriptors(Class<?> clazz, Map<String, PropertyDescriptor> map) {
		if (clazz == Object.class) {
			return map;
		}
		descriptors(clazz.getSuperclass(), map);
		Stream.of(BeanUtils.getPropertyDescriptors(clazz)).forEach(pd -> {
			map.put(pd.getName(), pd);
		});
		return map;
	}

	private List<Field> fields(Class<?> clazz, List<Field> list) {
		if (clazz == Object.class) {
			return list;
		}
		fields(clazz.getSuperclass(), list);
		Stream.of(clazz.getDeclaredFields()).forEach(field -> {
			list.add(field);
		});
		return list;
	}

}
