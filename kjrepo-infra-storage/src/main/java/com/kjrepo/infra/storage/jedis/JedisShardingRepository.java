package com.kjrepo.infra.storage.jedis;

import com.kjrepo.infra.common.executor.LazyInfoExecutor;
import com.kjrepo.infra.storage.utils.JedisUtils;

import redis.clients.jedis.JedisSharding;

@SuppressWarnings("deprecation")
public class JedisShardingRepository extends LazyInfoExecutor<JedisSharding, JedisShardingInfo> {

	public JedisShardingRepository(JedisShardingInfo info) {
		super(info, () -> JedisUtils.jedisSharding(info));
	}

}
