package com.kjrepo.infra.storage.jedis;

import com.kjrepo.infra.executor.lazy.LazyExecutor;
import com.kjrepo.infra.storage.utils.JedisUtils;

import redis.clients.jedis.JedisSharding;

@SuppressWarnings("deprecation")
public class JedisShardingRepository extends LazyExecutor<JedisSharding, JedisShardingInfo> {

	public JedisShardingRepository(JedisShardingInfo info) {
		super(info, () -> JedisUtils.jedisSharding(info));
	}

}
