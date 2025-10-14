package com.kjrepo.infra.text.json.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.annimon.stream.Collectors;
import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

public class TypeMapperUtils {

	public static Map<Class<?>, Map<Type, Type>> mapper(Class<?> type, Map<Type, Type> mapper) {
		if (mapper.size() == 0) {
			return mapper(type);
		} else {
			Map<Class<?>, Map<Type, Type>> mappers = Maps.newHashMap();
			mappers.put(type, mapper);
			Stream.ofNullable(type.getGenericInterfaces()).forEach(i -> mapper(type, i, mappers));
			return mapper(type, type.getGenericSuperclass(), mappers);
		}
	}

	public static Map<Class<?>, Map<Type, Type>> mapper(Class<?> type) {
		return cache.getUnchecked(type);
	}

	public static Type mapper(Map<Class<?>, Map<Type, Type>> mapper, Class<?> clazz, Type type) {
		return mapper.getOrDefault(clazz, Maps.newHashMap()).getOrDefault(type, type);
	}

	private static Map<Class<?>, Map<Type, Type>> mapper(Class<?> type, Type superType,
			Map<Class<?>, Map<Type, Type>> mapper) {
		if (superType == null || superType == Object.class) {
			return mapper;
		}

		if (superType instanceof Class<?>) {
			Class<?> superClass = (Class<?>) superType;
			Stream.ofNullable(superClass.getGenericInterfaces()).forEach(i -> mapper(superClass, i, mapper));
			return mapper(superClass, superClass.getGenericSuperclass(), mapper);
		} else if (superType instanceof ParameterizedType) {
			return mapper(type, (ParameterizedType) superType, mapper);
		} else {
			return mapper;
		}
	}

	private static Map<Class<?>, Map<Type, Type>> mapper(Class<?> type, ParameterizedType superType,
			Map<Class<?>, Map<Type, Type>> mapper) {
		Class<?> rawType = (Class<?>) superType.getRawType();
		Type[] varis = rawType.getTypeParameters();
		Type[] types = superType.getActualTypeArguments();
		mapper.put(rawType, IntStream.range(0, types.length).mapToObj(i -> i)
				.collect(Collectors.toMap(i -> varis[i], i -> mapper(mapper, type, types[i]))));
		mapper(rawType, rawType.getGenericSuperclass(), mapper);
		Stream.ofNullable(rawType.getGenericInterfaces()).forEach(i -> mapper(rawType, i, mapper));
		return mapper;
	}

	private static final LoadingCache<Class<?>, Map<Class<?>, Map<Type, Type>>> cache = CacheBuilder.newBuilder()
			.build(new CacheLoader<>() {
				@Override
				public Map<Class<?>, Map<Type, Type>> load(Class<?> key) throws Exception {
					Map<Class<?>, Map<Type, Type>> mapper = Maps.newHashMap();
					Stream.ofNullable(key.getGenericInterfaces()).forEach(i -> mapper(key, i, mapper));
					return mapper(key, key.getGenericSuperclass(), mapper);
				}
			});
}
