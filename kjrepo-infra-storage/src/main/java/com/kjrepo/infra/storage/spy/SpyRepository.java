package com.kjrepo.infra.storage.spy;

import com.kjrepo.infra.common.executor.LazyInfoExecutor;
import com.kjrepo.infra.storage.utils.SpyUtils;

import net.spy.memcached.MemcachedClient;

public class SpyRepository extends LazyInfoExecutor<MemcachedClient, SpyInfo> {

	public SpyRepository(SpyInfo info) {
		super(info, () -> SpyUtils.client(info));
	}

}
