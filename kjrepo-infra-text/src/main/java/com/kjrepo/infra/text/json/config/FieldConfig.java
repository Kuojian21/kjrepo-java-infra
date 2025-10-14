package com.kjrepo.infra.text.json.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;
import com.kjrepo.infra.text.json.ConfigUtils;

public class FieldConfig extends AbstractCacheConfig<Field> {

	public FieldConfig(Map<Class<?>, Map<Type, Type>> mapper) {
		super(clazz -> new CacheLoader<Class<?>, Map<String, Field>>() {
			@Override
			public Map<String, Field> load(Class<?> clazz) throws Exception {
				Map<String, Field> fields = Maps.newHashMap();
				if (clazz.getSuperclass() == null || clazz.getSuperclass().equals(Object.class)) {

				} else {
					fields.putAll(load(clazz.getSuperclass()));
				}
				fields.putAll(Stream.of(clazz.getDeclaredFields()).filter(
						field -> !Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()))
						.collect(Collectors.toMap(field -> field.getName(), field -> field)));
				return fields;
			}
		}.load(clazz), mapper);
	}

	@Override
	public <V> V setValue(V obj, Field field, Object json) throws Exception {
		if (obj == null || field == null) {
			return obj;
		}
		field.setAccessible(true);
		field.set(obj,
				ConfigUtils.valueUnchecked(json, field.getGenericType(), this.mapper().get(field.getDeclaringClass())));
		return obj;
	}

}
