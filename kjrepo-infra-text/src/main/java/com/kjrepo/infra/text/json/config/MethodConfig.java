package com.kjrepo.infra.text.json.config;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;
import com.kjrepo.infra.text.json.utils.ArgsUtils;

public class MethodConfig extends AbstractCacheConfig<Method> {

	public MethodConfig(Map<Class<?>, Map<Type, Type>> mapper) {
		super(clazz -> new CacheLoader<Class<?>, Map<String, Method>>() {

			@Override
			public Map<String, Method> load(Class<?> clazz) throws Exception {
				Map<String, Method> methods = Maps.newHashMap();
				if (clazz.getSuperclass() != null) {
					methods.putAll(load(clazz.getSuperclass()));
				}
				methods.putAll(Stream.of(clazz.getDeclaredMethods()).collect(Collectors.toMap(m -> sign(m), m -> m)));
				return methods;
			}

		}.load(clazz), mapper);
	}

	public static String sign(Method method) {
		StringBuilder builder = new StringBuilder();
		builder.append(method.getName()).append("(");
		builder.append(StringUtils.join(
				Stream.of(method.getGenericParameterTypes()).map(p -> p.getTypeName()).collect(Collectors.toList()),
				",")).append(")");
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> V setValue(V obj, Method method, Object json) throws Exception {
		return (V) method.invoke(obj, ArgsUtils.args(obj, method.getGenericParameterTypes(), json,
				this.mapper().get(method.getDeclaringClass())));
	}

}
