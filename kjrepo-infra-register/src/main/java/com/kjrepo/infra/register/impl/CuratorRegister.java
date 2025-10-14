package com.kjrepo.infra.register.impl;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import com.google.common.collect.Maps;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.register.AbstractRegister;
import com.kjrepo.infra.text.json.JsonUtils;

public class CuratorRegister<V> extends AbstractRegister<V> {

	private final Map<String, LazySupplier<CuratorCache>> caches = Maps.newConcurrentMap();
	private final CuratorFramework curator;

	public CuratorRegister(Class<V> clazz, CuratorFramework curator) {
		super(clazz);
		this.curator = curator;
	}

	@Override
	public void set(String key, V value) {
		byte[] bytes = JsonUtils.toJson(value).getBytes(StandardCharsets.UTF_8);
		try {
			curator.setData().forPath(key, bytes);
		} catch (KeeperException.NoNodeException e) {
			try {
				curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(key, bytes);
			} catch (KeeperException.NodeExistsException nee) {
			} catch (Exception ee) {
				throw new RuntimeException(ee);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void init(String path) {
		CuratorCache cache = caches
				.computeIfAbsent(path, k -> LazySupplier.wrap(() -> CuratorCache.build(this.curator, path))).get();
		cache.listenable().addListener(CuratorCacheListener.builder() //
				.forCreates(node -> logger.info("creates:{}", node.getPath())) //
				.forChanges((oldNode, node) -> refresh(node.getPath())) //
				.forDeletes(node -> logger.info("deletes:{}", node.getPath())) //
				.forInitialized(() -> logger.info("initialized}")).build());
		cache.start();
	}

	@Override
	protected Object json(String path) {
		return JsonUtils.fromJson(caches
				.computeIfAbsent(path, k -> LazySupplier.wrap(() -> CuratorCache.build(this.curator, path))).get()
				.get(path).map(data -> new String(data.getData(), StandardCharsets.UTF_8)).orElse(super.defString()),
				Object.class);
	}
}
