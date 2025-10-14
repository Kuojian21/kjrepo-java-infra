package com.kjrepo.infra.distrib.cache;

import org.apache.commons.lang3.StringUtils;

import com.google.common.cache.LoadingCache;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.utils.ProxyUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;

public interface DLoadingCache<K, V> extends LoadingCache<K, V> {

	@SuppressWarnings("unchecked")
	public static <K, V> DLoadingCache<K, V> wrap(String key, LoadingCache<K, V> cache) {
		if (StringUtils.isEmpty(key)) {
			return (DLoadingCache<K, V>) ProxyUtils.proxy(DLoadingCache.class, (obj, method, args, proxy) -> {
				if (method.getDeclaringClass() == DLoadingCache.class && "refresh".equals(method.getName())
						&& method.getParameterCount() == 0) {
					return null;
				}
				return method.invoke(cache, args);
			});
		}
		StackTraceElement element = Thread.currentThread().getStackTrace()[2];
		if (!"<clinit>".equals(element.getMethodName()) && !"<init>".equals(element.getMethodName())) {
			LoggerUtils.logger(DLoadingCache.class).error(
					"please invoke this method in <clinit> or <init> method!!! location:{}.{}:{}",
					element.getClassName(), element.getMethodName(), element.getLineNumber());
		}
		Register<Long> register = RegisterFactory.getContext(element.getClassName()).getRegister(Long.class);
		register.addListener(key, e -> {
			cache.invalidateAll();
		});
		register.get(key);
		return (DLoadingCache<K, V>) ProxyUtils.proxy(DLoadingCache.class, (obj, method, args, proxy) -> {
			if (method.getDeclaringClass() == DLoadingCache.class && "refresh".equals(method.getName())
					&& method.getParameterCount() == 0) {
				LoggerUtils.logger(DLoadingCache.class).info("old key:{} value:{}", key, register.get(key));
				register.set(key, System.currentTimeMillis());
				return null;
			}
			return method.invoke(cache, args);
		});
	}

	void refresh();

}
