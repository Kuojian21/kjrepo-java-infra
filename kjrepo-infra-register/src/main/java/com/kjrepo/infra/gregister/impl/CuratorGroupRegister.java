package com.kjrepo.infra.gregister.impl;

import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

import com.google.common.collect.Maps;
import com.kjrepo.infra.common.info.Pair;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.gregister.AbstractGroupReigster;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.RegisterListener;
import com.kjrepo.infra.register.impl.CuratorRegister;

public class CuratorGroupRegister<V, I> extends AbstractGroupReigster<V, I> {

	private final Register<V> vregister;
	private final Map<String, LazySupplier<CuratorCache>> caches = Maps.newConcurrentMap();
	private final CuratorFramework curator;

	public CuratorGroupRegister(Class<V> vclazz, Class<I> clazz, CuratorFramework curator) {
		super(new CuratorRegister<>(clazz, curator));
		this.curator = curator;
		this.vregister = new CuratorRegister<>(vclazz, curator);
	}

	@Override
	protected void init(String path) {
		CuratorCache cache = caches
				.computeIfAbsent(path, k -> LazySupplier.wrap(() -> CuratorCache.build(this.curator, path))).get();
		cache.listenable().addListener(CuratorCacheListener.builder()
				.forCreates(node -> fireCreate(path, node.getPath().substring(node.getPath().lastIndexOf("/") + 1)))
				.forChanges((oldNode, node) -> logger.info("changes:", node.getPath()))
				.forDeletes(node -> fireRemove(path, node.getPath().substring(node.getPath().lastIndexOf("/") + 1)))
				.forInitialized(() -> logger.info("initialized")).build());
		cache.start();
	}

	@Override
	protected List<Pair<String, I>> data(String path) {
		return caches.computeIfAbsent(path, k -> LazySupplier.wrap(() -> CuratorCache.build(this.curator, path))).get()
				.stream().filter(node -> !node.getPath().equals(path) || node.getData() != null)
				.map(node -> node.getPath().substring(node.getPath().lastIndexOf("/")))
				.map(ckey -> Pair.pair(ckey, get(path, ckey))).toList();
	}

	@Override
	public V get(String key) {
		return this.vregister.get(key);
	}

	@Override
	public void set(String key, V value) {
		this.vregister.set(key, value);
	}

	@Override
	public void addListener(String key, RegisterListener<V> listener) {
		this.vregister.addListener(key, listener);
	}

}
