package com.kjrepo.infra.text.json.mapper;

import java.lang.reflect.Type;
import java.util.Map;

import com.annimon.stream.Optional;
import com.google.common.collect.Maps;

public class Mapper {

	private static final ThreadLocal<Map<Class<?>, Map<Type, Type>>> MAP = ThreadLocal
			.withInitial(() -> Maps.newHashMap());

	public static Type mapper(Class<?> clazz, Mapper mapper, Type type, Map<Type, Type> map) {
		MAP.get().put(clazz, map);
		try {
			return mapper.mapper(type);
		} finally {
			MAP.get().remove(clazz);
		}
	}

	private Type mapper(Type type) {
		Type mtype = this.mapper.getOrDefault(type, type);
		if (this.child == null) {
			return MAP.get().getOrDefault(this.clazz, Maps.newHashMap()).getOrDefault(mtype, mtype);
		} else {
			return this.child.mapper(mtype);
		}
	}

	private final Map<Type, Type> mapper;
	private final Class<?> clazz;
	private final Mapper child;

	public Mapper(Class<?> clazz, Map<Type, Type> mapper) {
		this(clazz, mapper, null);
	}

	public Mapper(Class<?> clazz, Map<Type, Type> mapper, Mapper child) {
		super();
		this.clazz = clazz;
		this.mapper = Optional.ofNullable(mapper).orElseGet(() -> Maps.newHashMap());
		this.child = child;
	}

}
