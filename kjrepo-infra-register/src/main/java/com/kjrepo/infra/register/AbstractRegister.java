package com.kjrepo.infra.register;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.text.json.ConfigUtils;

public abstract class AbstractRegister<V> implements Register<V> {

	protected final Logger logger = LoggerUtils.logger(Register.class);

	private final ConcurrentMap<String, Data> datas = Maps.newConcurrentMap();
	private final Class<V> clazz;

	public AbstractRegister(Class<V> clazz) {
		this.clazz = clazz;
	}

	@Override
	public final V get(String key) {
		return getData(key).data.get().get();
	}

	private Data getData(String key) {
		return datas.computeIfAbsent(key, Data::new);
	}

	@Override
	public final void addListener(String key, RegisterListener<V> listener) {
		logger.debug("add listener for [{}]!!!", key);
		getData(key).listeners.add(listener);
	}

	protected void refresh(String key) {
//		synchronized (getData(key)) {
		V oData = this.get(key);
		datas.get(key).data.get().refresh();
		V nData = this.get(key);

		RegisterEvent<V> event = new RegisterEvent<V>();
		event.setKey(key);

		if (Objects.equal(oData, nData)) {

		} else {
			logger.info("fireChange key:[{}]!!!", event.getKey());
			this.getData(event.getKey()).listeners.forEach(listerner -> listerner.onChange(event));
		}
//		}
	}

	protected String defString() {
		if (this.clazz.isPrimitive() || Number.class.isAssignableFrom(this.clazz) || this.clazz == Character.class
				|| this.clazz == Boolean.class || this.clazz == String.class) {
			return null;
		} else if (this.clazz.isArray() || List.class.isAssignableFrom(this.clazz)
				|| Set.class.isAssignableFrom(this.clazz)) {
			return "[]";
		}
		return "{}";
	}

	protected abstract void init(String path);

	protected abstract Object json(String path);

	class Data {
		final Set<RegisterListener<V>> listeners = Sets.newConcurrentHashSet();
		final String path;
		final LazySupplier<LazySupplier<V>> data;

		Data(String path) {
			super();
			this.path = path;
			this.data = LazySupplier.wrap(() -> {
				logger.info("The path:[{}] is initing!!!", path);
				init(path);
				return LazySupplier.wrap(() -> {
					return ConfigUtils.valueUnchecked(json(path), clazz);
				});
			});
		}
	}

}
