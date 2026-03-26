package com.kjrepo.infra.storage.jedis;

import java.util.List;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Connection;

public class JedisShardingInfo {

	private List<Map<String, Object>> shards;
	private Map<String, Object> clientConfig;
	private GenericObjectPoolConfig<Connection> poolConfig;
	private String algo;
	private String tagPattern;

	public List<Map<String, Object>> getShards() {
		return shards;
	}

	public void setShards(List<Map<String, Object>> shards) {
		this.shards = shards;
	}

	public Map<String, Object> getClientConfig() {
		return clientConfig;
	}

	public void setClientConfig(Map<String, Object> clientConfig) {
		this.clientConfig = clientConfig;
	}

	public GenericObjectPoolConfig<Connection> getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(GenericObjectPoolConfig<Connection> poolConfig) {
		this.poolConfig = poolConfig;
	}

	public String getAlgo() {
		return algo;
	}

	public void setAlgo(String algo) {
		this.algo = algo;
	}

	public String getTagPattern() {
		return tagPattern;
	}

	public void setTagPattern(String tagPattern) {
		this.tagPattern = tagPattern;
	}

}
