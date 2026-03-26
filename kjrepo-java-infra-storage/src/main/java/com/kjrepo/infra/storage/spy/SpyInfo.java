package com.kjrepo.infra.storage.spy;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SpyInfo {

	private List<Map<String, Object>> addrs = Lists.newArrayList();
	private Map<String, Object> connectionFactory = Maps.newHashMap();

	public List<Map<String, Object>> getAddrs() {
		return addrs;
	}

	public void setAddrs(List<Map<String, Object>> addrs) {
		this.addrs = addrs;
	}

	public Map<String, Object> getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(Map<String, Object> connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

}