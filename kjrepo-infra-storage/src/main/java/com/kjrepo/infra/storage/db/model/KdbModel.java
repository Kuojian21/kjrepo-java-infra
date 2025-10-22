package com.kjrepo.infra.storage.db.model;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.base.CaseFormat;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.info.Pair;

public class KdbModel {

	public static KdbModel of(Class<?> clazz) {
		return repo.getUnchecked(clazz);
	}

	public String table() {
		if (kdbTable != null && StringUtils.isNotEmpty(kdbTable.name())) {
			return kdbTable.name();
		}
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.name);
	}

	public String name() {
		return name;
	}

	public KdbTable kdbTable() {
		return kdbTable;
	}

	public List<KdbProperty> properties() {
		return propertyList;
	}

	public KdbProperty getProperty(String name) {
		return this.propertyMap.get(name);
	}

	private static Map<String, PropertyDescriptor> descriptors(Class<?> clazz, Map<String, PropertyDescriptor> map) {
		if (clazz == Object.class) {
			return map;
		}
		descriptors(clazz.getSuperclass(), map);
		Stream.of(BeanUtils.getPropertyDescriptors(clazz)).forEach(pd -> {
			map.put(pd.getName(), pd);
		});
		return map;
	}

	private static List<Field> fields(Class<?> clazz, List<Field> list) {
		if (clazz == Object.class) {
			return list;
		}
		fields(clazz.getSuperclass(), list);
		Stream.of(clazz.getDeclaredFields()).forEach(field -> {
			list.add(field);
		});
		return list;
	}

	private static final LoadingCache<Class<?>, KdbModel> repo = CacheBuilder.newBuilder().build(new CacheLoader<>() {
		@Override
		public KdbModel load(Class<?> key) throws Exception {
			return new KdbModel(key);
		}
	});

	private final String name;
	private final KdbTable kdbTable;
	private final List<KdbProperty> propertyList;
	private final Map<String, KdbProperty> propertyMap;

	public KdbModel(Class<?> clazz) {
		this.name = clazz.getSimpleName();
		this.kdbTable = clazz.getAnnotation(KdbTable.class);
		Map<String, PropertyDescriptor> pdmap = descriptors(clazz, Maps.newHashMap());
		this.propertyList = Stream.of(fields(clazz, Lists.newArrayList()))
				.map(f -> KdbProperty.of(f, pdmap.get(f.getName()))).toList();
		this.propertyMap = Stream.of(this.propertyList)
				.flatMap(p -> Stream.of(Lists.newArrayList(Pair.pair(p.name(), p), Pair.pair(p.column(), p))))
				.distinct().collect(Collectors.toMap(Pair::getKey, Pair::getValue));
	}
}
