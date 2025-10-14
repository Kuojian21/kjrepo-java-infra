package com.kjrepo.infra.storage.jedis;

import com.kjrepo.infra.common.executor.LazyInfoExecutor;
import com.kjrepo.infra.storage.utils.JedisUtils;

import redis.clients.jedis.JedisPool;

public class JedisRepository extends LazyInfoExecutor<JedisPool, JedisInfo> {

	public JedisRepository(JedisInfo info) {
		super(info, () -> JedisUtils.jedis(info));
	}

}
