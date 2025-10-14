package com.kjrepo.infra.text.json;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;

import com.annimon.stream.Optional;
import com.google.common.collect.Maps;
import com.kjrepo.infra.text.json.config.ConfigFacade;
import com.kjrepo.infra.text.json.utils.TypeMapperUtils;
import com.kjrepo.infra.text.json.value.ClassValue;
import com.kjrepo.infra.text.json.value.GenericArrayTypeValue;
import com.kjrepo.infra.text.json.value.JacksonValue;
import com.kjrepo.infra.text.json.value.ParameterizedTypeValue;

public class ConfigUtils {

	public static <T> T config(T obj, Object jsons) {
		return config(obj, jsons, Maps.newHashMap());
	}

	public static <T> T config(T obj, Object jsons, Map<Type, Type> mapper) {
		if (obj == null || jsons == null) {
			return obj;
		}
		ConfigFacade config = new ConfigFacade(TypeMapperUtils.mapper(obj.getClass(), mapper));
		config.config(obj, jsons);
		return obj;
	}

	public static <T> T valueUnchecked(Object json, Type type) {
		return valueUnchecked(json, type, Maps.newHashMap());
	}

	public static <T> T valueUnchecked(Object json, Type type, Map<Type, Type> mapper) {
		try {
			return value(json, type, mapper);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T value(Object json, Type type, Map<Type, Type> mapper) throws Exception {
		if (type == null || json == null) {
			return (T) json;
		}
		mapper = Optional.ofNullable(mapper).orElseGet(() -> Maps.newHashMap());
		type = mapper.getOrDefault(type, type);
		if (type instanceof Class) {
			Class<T> objType = (Class<T>) type;
			return new ClassValue().value(json, objType);
		} else if (type instanceof ParameterizedType) {
			ParameterizedType objType = (ParameterizedType) type;
			return new ParameterizedTypeValue(mapper).value(json, objType);
		} else if (type instanceof GenericArrayType) {
			GenericArrayType objType = (GenericArrayType) type;
			return new GenericArrayTypeValue(mapper).value(json, objType);
		} else if (type instanceof TypeVariable<?>) {
			TypeVariable<?> objType = (TypeVariable<?>) type;
			return new JacksonValue().value(json, objType);
		} else if (type instanceof WildcardType) {
			WildcardType objType = (WildcardType) type;
			return new JacksonValue().value(json, objType);
		} else {
			return new JacksonValue().value(json, type);
		}
	}

}
