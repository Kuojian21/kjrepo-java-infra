package com.kjrepo.infra.text.json.config;

import java.lang.reflect.Type;
import java.util.Map;

public abstract class AbstractConfig implements Config {

	private final Map<Class<?>, Map<Type, Type>> mapper;

	public AbstractConfig(Map<Class<?>, Map<Type, Type>> mapper) {
		super();
		this.mapper = mapper;
	}

	public Map<Class<?>, Map<Type, Type>> mapper() {
		return this.mapper;
	}

}
