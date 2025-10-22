package com.kjrepo.infra.distrib.cache;

import org.apache.commons.lang3.StringUtils;

import com.google.common.cache.LoadingCache;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.utils.StackUtils;
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
		Register<Long> register = RegisterFactory.getContext(StackUtils.firstBusinessInvokerClassname()).getRegister(Long.class);
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
