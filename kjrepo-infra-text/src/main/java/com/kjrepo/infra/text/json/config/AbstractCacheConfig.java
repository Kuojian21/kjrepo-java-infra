package com.kjrepo.infra.text.json.config;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;

import com.annimon.stream.function.ThrowableFunction;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.text.json.JsonUtils;

public abstract class AbstractCacheConfig<E> extends AbstractConfig implements Config {

	private static final ConcurrentMap<Class<?>, LoadingCache<Class<?>, Map<String, ?>>> cacheMap = Maps
			.newConcurrentMap();

	protected final Logger logger = LoggerUtils.logger(this.getClass());
	protected final LoadingCache<Class<?>, Map<String, ?>> cache;

	protected AbstractCacheConfig(ThrowableFunction<Class<?>, Map<String, E>, Exception> lodader,
			Map<Class<?>, Map<Type, Type>> mapper) {
		super(mapper);
		cacheMap.computeIfAbsent(getClass(),
				clazz -> CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, Map<String, ?>>() {
					@Override
					public Map<String, E> load(Class<?> key) throws Exception {
						return lodader.apply(key);
					}
				}));
		this.cache = cacheMap.get(this.getClass());

	}

	@Override
	public final <V> V config(V obj, List<Map<String, Object>> jsons) {
		if (obj == null || jsons == null) {
			return obj;
		}
		Class<?> clazz = obj.getClass();
		jsons.forEach(valueMap -> valueMap.forEach((name, params) -> {
			try {
				E element = element(clazz, name);
				if (element == null) {
					logger.error("no {} : {}", this.getClass().getName(), name);
				} else {
					setValue(obj, element, params);
				}
			} catch (Exception e) {
				logger.error("exception name:{} params:{}", name, JsonUtils.toPrettyJson(params), e);
			}
		}));
		return obj;
	}

	protected boolean canSet(Class<?> clazz, String name) {
		return cache.getUnchecked(clazz).get(name) != null;
	}

	public <V> boolean setValue(Class<?> clazz, String name, V obj, Object json) throws Exception {
		if (canSet(clazz, name)) {
			this.setValue(obj, element(clazz, name), json);
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public E element(Class<?> clazz, String name) {
		return (E) cache.getUnchecked(clazz).get(name);
	}

	public abstract <V> V setValue(V obj, E element, Object json) throws Exception;

}
