package com.kjrepo.infra.text.json.value;

import java.lang.reflect.Type;
import java.util.Map;

import com.annimon.stream.Optional;
import com.google.common.collect.Maps;

public abstract class AbstractValue<T extends Type> implements Value<T> {

	private final Map<Type, Type> mapper;

	public AbstractValue(Map<Type, Type> mapper) {
		super();
		this.mapper = Optional.ofNullable(mapper).orElseGet(() -> Maps.newHashMap());
	}

	public Map<Type, Type> mapper() {
		return mapper;
	}

}
