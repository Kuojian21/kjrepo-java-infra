package com.kjrepo.infra.register.group;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.RegisterListener;

public abstract class AbstractGroupReigster<V, I> implements GroupRegister<V, I> {

	protected final Logger logger = LoggerUtils.logger(GroupRegister.class);

	private final ConcurrentMap<String, Data> datas = Maps.newConcurrentMap();
	private final Register<I> cregister;

	public AbstractGroupReigster(Register<I> register) {
		this.cregister = register;
	}

	@Override
	public final Map<String, I> cget(String pkey) {
		return Stream.of(cgetData(pkey).data.get().get())
				.collect(Collectors.toMap(p -> p, this.cregister::get, Maps::newLinkedHashMap));
	}

	private Data cgetData(String pkey) {
		return this.datas.computeIfAbsent(pkey, Data::new);
	}

	@Override
	public final List<String> ckeys(String pkey) {
		return cgetData(pkey).data.get().get();
	}

	@Override
	public void cset(String ckey, I value) {
		this.cregister.set(ckey, value);
	}

	@Override
	public void caddListener(String pkey, GroupRegisterListener listener) {
		logger.debug("add listener for [{}]'s gourp!!!", pkey);
		cgetData(pkey).plisteners.add(listener);
	}

	@Override
	public void caddListener(String pkey, RegisterListener<I> listener) {
		logger.debug("add listener for [{}]'s children!!!", pkey);
		Data data = cgetData(pkey);
		data.clisteners.add(listener);
	}

	protected void fireCreate(String pkey, String ckey) {
		logger.info("fireCreate,pkey:[{}] ckey:[{}]!!!", pkey, ckey);
		Data data = cgetData(pkey);
//		try {
//			data.write.lock();
		data.data.refresh();
		data.plisteners.forEach(listener -> {
			listener.onCreate(ckey);
		});
//		} finally {
//			data.write.unlock();
//		}
	}

	protected void fireRemove(String pkey, String ckey) {
		logger.info("fireRemove,pkey:[{}] ckey:[{}]!!!", pkey, ckey);
		Data data = cgetData(pkey);
//		try {
//			data.write.lock();
		data.data.refresh();
		data.plisteners.forEach(listener -> {
			listener.onRemove(ckey);
		});
//		} finally {
//			data.write.unlock();
//		}
	}

	protected abstract void init(String path);

	protected abstract List<String> keys(String path);

	class Data {

		final Set<GroupRegisterListener> plisteners = Sets.newConcurrentHashSet();
		final Set<RegisterListener<I>> clisteners = Sets.newConcurrentHashSet();
//		final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
//		final Lock read = lock.readLock();
//		final Lock write = lock.writeLock();
		final String path;
		final LazySupplier<LazySupplier<List<String>>> data;

		Data(String path) {
			super();
			this.path = path;
			this.data = LazySupplier.wrap(() -> {
				logger.info("The group path:[{}] is initing!!!", path);
				init(path);
				return LazySupplier.wrap(() -> {
					List<String> keys = keys(path);
					keys.forEach(key -> {
						clisteners.forEach(listener -> {
							cregister.addListener(key, listener);
						});
					});
					return keys;
				});
			});
		}
	}

}
