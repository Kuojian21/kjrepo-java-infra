package com.kjrepo.infra.network.okhttp;

import java.util.Map;

import com.google.common.collect.Maps;

public class OkhttpInfo {

	private Map<String, Object> dispatcher = Maps.newHashMap();
	private Map<String, Object> data = Maps.newHashMap();

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public Map<String, Object> getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(Map<String, Object> dispatcher) {
		this.dispatcher = dispatcher;
	}

}
