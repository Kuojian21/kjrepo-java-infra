package com.kjrepo.infra.text.json.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kjrepo.infra.text.json.utils.ArgsUtils;

public class ConstructorConfig extends AbstractCacheConfig<Constructor<?>> {

	public ConstructorConfig(Map<Class<?>, Map<Type, Type>> mapper) {
		super(clazz -> Stream.of(clazz.getDeclaredConstructors()).collect(Collectors.toMap(m -> sign(m), m -> m)),
				mapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> V setValue(V obj, Constructor<?> constructor, Object json) throws Exception {
		return (V) constructor.newInstance(ArgsUtils.args(obj, constructor.getGenericParameterTypes(), json,
				this.mapper().get(constructor.getDeclaringClass())));
	}

	public static String sign(Constructor<?> constructor) {
		StringBuilder builder = new StringBuilder();
		builder.append("(").append(StringUtils.join(Stream.of(constructor.getGenericParameterTypes())
				.map(p -> p.getTypeName()).collect(Collectors.toList()), ",")).append(")");
		return builder.toString();
	}

}
