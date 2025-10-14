package com.kjrepo.infra.register;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.text.json.ConfigUtils;

public abstract class AbstractRegister<V> implements Register<V> {

	protected final Logger logger = LoggerUtils.logger(this.getClass());

	private final ConcurrentMap<String, LazySupplier<LazySupplier<V>>> datas = Maps.newConcurrentMap();
	private final ConcurrentMap<String, Set<RegisterListener<V>>> listeners = Maps.newConcurrentMap();
	private final Class<V> clazz;

	public AbstractRegister(Class<V> clazz) {
		super();
		this.clazz = clazz;
	}

	@Override
	public V get(String key) {
		return (V) datas.computeIfAbsent(key, k -> LazySupplier.wrap(() -> {
			this.init(key);
			return LazySupplier.wrap(() -> {
				return ConfigUtils.valueUnchecked(json(key), clazz);
			});
		})).get().get();
	}

	@Override
	public void addListener(String key, RegisterListener<V> listener) {
		listeners.computeIfAbsent(key, k -> Sets.newConcurrentHashSet()).add(listener);
		logger.info("add listener for {} ", key);
	}

	protected void refresh(String key) {
		V o = this.get(key);
		datas.get(key).get().refresh();
		V n = this.get(key);

		RegisterEvent<V> event = new RegisterEvent<V>();
		event.setKey(key);
		event.setOldData(o);
		event.setNewData(n);

		this.fireListener(event);
	}

	protected void fireListener(RegisterEvent<V> event) {
		if (event.getOldData() == event.getNewData()) {

		} else if (event.getOldData() == null || event.getNewData() == null) {
			listeners.computeIfAbsent(event.getKey(), k -> Sets.newConcurrentHashSet())
					.forEach(listerner -> listerner.onChange(event));
		} else if (event.getOldData().equals(event.getNewData())) {

		} else {
			listeners.computeIfAbsent(event.getKey(), k -> Sets.newConcurrentHashSet())
					.forEach(listerner -> listerner.onChange(event));
		}
	}

	protected String defString() {
		if (this.clazz.isPrimitive() || Number.class.isAssignableFrom(this.clazz) || this.clazz == String.class) {
			return "";
		} else if (this.clazz.isArray() || List.class.isAssignableFrom(this.clazz)) {
			return "[]";
		}
		return "{}";
	}

	protected abstract void init(String path);

	protected abstract Object json(String path);

}
