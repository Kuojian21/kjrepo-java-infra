package com.kjrepo.infra.storage.jedis;

import java.util.Map;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.google.common.collect.Maps;

import redis.clients.jedis.Jedis;

public class JedisInfo {
	private Map<String, Object> hostAndPort = Maps.newHashMap();;
	private Map<String, Object> clientConfig = Maps.newHashMap();;
	private GenericObjectPoolConfig<Jedis> poolConfig;

	public Map<String, Object> getHostAndPort() {
		return hostAndPort;
	}

	public void setHostAndPort(Map<String, Object> hostAndPort) {
		this.hostAndPort = hostAndPort;
	}

	public Map<String, Object> getClientConfig() {
		return clientConfig;
	}

	public void setClientConfig(Map<String, Object> clientConfig) {
		this.clientConfig = clientConfig;
	}

	public GenericObjectPoolConfig<Jedis> getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(GenericObjectPoolConfig<Jedis> poolConfig) {
		this.poolConfig = poolConfig;
	}
}