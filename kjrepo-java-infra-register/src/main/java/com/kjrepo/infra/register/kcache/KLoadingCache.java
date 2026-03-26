package com.kjrepo.infra.register.kcache;

import org.apache.commons.lang3.StringUtils;

import com.google.common.cache.LoadingCache;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.utils.ProxyUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;

public interface KLoadingCache<K, V> extends LoadingCache<K, V> {

	@SuppressWarnings("unchecked")
	public static <K, V> KLoadingCache<K, V> wrap(String key, LoadingCache<K, V> cache) {
		if (StringUtils.isEmpty(key)) {
			return (KLoadingCache<K, V>) ProxyUtils.proxy(KLoadingCache.class, (obj, method, args, proxy) -> {
				if (method.getDeclaringClass() == KLoadingCache.class && "refresh".equals(method.getName())
						&& method.getParameterCount() == 0) {
					return null;
				}
				return method.invoke(cache, args);
			});
		}
		Register<Long> register = RegisterFactory.getContext().getRegister(Long.class);
		register.addListener(key, e -> {
			cache.invalidateAll();
		});
		register.get(key);
		return (KLoadingCache<K, V>) ProxyUtils.proxy(KLoadingCache.class, (obj, method, args, proxy) -> {
			if (method.getDeclaringClass() == KLoadingCache.class && "refresh".equals(method.getName())
					&& method.getParameterCount() == 0) {
				LoggerUtils.logger(KLoadingCache.class).info("old key:{} value:{}", key, register.get(key));
				register.set(key, System.currentTimeMillis());
				return null;
			}
			return method.invoke(cache, args);
		});
	}

	void refresh();

}
