package com.kjrepo.infra.common.bean;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class BeanMapBuilder<T> extends BeanBuilder<T> {

	public static <T> BeanMapBuilder<T> builder(Class<T> clazz, Map<String, Object> data) {
		return new BeanMapBuilder<>(cache.getUnchecked(clazz).newProxy(data), data);
	}

	private static final LoadingCache<Class<?>, Holder> cache = CacheBuilder.newBuilder()
			.build(new CacheLoader<Class<?>, Holder>() {

				@Override
				public Holder load(Class<?> clazz) throws Exception {
					return new Holder(clazz);
				}

			});

	static class Holder {
		private final Map<Method, String> methods;
		private final Class<?> proxyClass;

		public Holder(Class<?> clazz) {
			try {
				methods = Arrays.stream(Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors())
						.collect(Collectors.toMap(PropertyDescriptor::getWriteMethod, PropertyDescriptor::getName));
				Enhancer enhancer = new Enhancer();
				enhancer.setSuperclass(clazz);
				enhancer.setCallbackType(MethodInterceptor.class);
				proxyClass = enhancer.createClass();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@SuppressWarnings({ "unchecked", "deprecation" })
		<T> T newProxy(Map<String, Object> data) {
			try {
				// thread-safe
				Enhancer.registerCallbacks(proxyClass,
						new Callback[] { (MethodInterceptor) (obj, method, args, proxy) -> {
							if (methods.containsKey(method)) {
								data.put(methods.get(method), args[0]);
							}
							return null;
						} });
				return (T) proxyClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			} finally {
				Enhancer.registerCallbacks(proxyClass, null);
			}
		}

	}

	private final Map<String, Object> data;

	public BeanMapBuilder(T bean, Map<String, Object> data) {
		super(bean);
		this.data = data;
	}

	public Map<String, Object> buildMap() {
		return data;
	}

}
