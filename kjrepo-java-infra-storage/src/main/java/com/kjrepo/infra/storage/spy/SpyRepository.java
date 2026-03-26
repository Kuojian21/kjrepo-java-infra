package com.kjrepo.infra.storage.spy;

import com.kjrepo.infra.executor.lazy.LazyExecutor;
import com.kjrepo.infra.storage.utils.SpyUtils;

import net.spy.memcached.MemcachedClient;

public class SpyRepository extends LazyExecutor<MemcachedClient, SpyInfo> {

	public SpyRepository(SpyInfo info) {
		super(info, () -> SpyUtils.client(info));
	}

}
