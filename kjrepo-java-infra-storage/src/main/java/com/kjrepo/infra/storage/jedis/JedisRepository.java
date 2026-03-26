package com.kjrepo.infra.storage.jedis;

import com.kjrepo.infra.executor.lazy.LazyExecutor;
import com.kjrepo.infra.storage.utils.JedisUtils;

import redis.clients.jedis.JedisPool;

public class JedisRepository extends LazyExecutor<JedisPool, JedisInfo> {

	public JedisRepository(JedisInfo info) {
		super(info, () -> JedisUtils.jedis(info));
	}

}
