package com.kjrepo.infra.storage.jedis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisFactory;

public class KjedisFactory extends JedisFactory {

	public KjedisFactory(HostAndPort hostAndPort, JedisClientConfig clientConfig) {
		super(hostAndPort, clientConfig);
	}

}
