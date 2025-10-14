package com.kjrepo.infra.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class ProxyUtils {

	public static <T> T proxy(Class<T> clazz, MethodInterceptor handler) {
		return jvm(clazz, handler);
	}

	@SuppressWarnings("unchecked")
	public static <T> T jvm(Class<T> clazz, MethodInterceptor handler) {
		return (T) Proxy.newProxyInstance(ProxyUtils.class.getClassLoader(), new Class<?>[] { clazz },
				(obj, method, args) -> handler.intercept(obj, method, args, null));
	}

	/**
	 * 
	 * --add-opens java.base/java.lang=ALL-UNNAMED
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cglib(Class<T> iClazz, MethodInterceptor handler) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(iClazz);
		enhancer.setCallbackType(MethodInterceptor.class);
		Class<?> clazz = enhancer.createClass();
		try {
			Enhancer.registerCallbacks(clazz, new Callback[] { handler });
			return (T) clazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		} finally {
			Enhancer.registerCallbacks(clazz, null);
		}
	}
}
