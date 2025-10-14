package com.kjrepo.infra.network.http;

import java.util.Map;

import com.google.common.collect.Maps;

public class KhttpClientInfo {
	private int defaultMaxPerRoute = 8;
	private int maxTotal = 512;
	private Map<String, Object> requestConfig = Maps.newHashMap();
	private Map<String, Object> socketConfig = Maps.newHashMap();

	public int getDefaultMaxPerRoute() {
		return defaultMaxPerRoute;
	}

	public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
		this.defaultMaxPerRoute = defaultMaxPerRoute;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public Map<String, Object> getRequestConfig() {
		return requestConfig;
	}

	public void setRequestConfig(Map<String, Object> requestConfig) {
		this.requestConfig = requestConfig;
	}

	public Map<String, Object> getSocketConfig() {
		return socketConfig;
	}

	public void setSocketConfig(Map<String, Object> socketConfig) {
		this.socketConfig = socketConfig;
	}

}