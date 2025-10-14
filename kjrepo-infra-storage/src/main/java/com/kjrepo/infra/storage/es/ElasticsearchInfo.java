package com.kjrepo.infra.storage.es;

import java.util.List;
import java.util.Map;

import org.elasticsearch.client.RestClient;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.executor.PooledInfoDefault;

public class ElasticsearchInfo extends PooledInfoDefault<RestClient> {
	private List<Map<String, Object>> httpHosts = Lists.newArrayList();
	private Map<String, Object> requestConfig = Maps.newHashMap();
	private HttpClientConfig httpClientConfig;

	public void setHttpHosts(List<Map<String, Object>> httpHosts) {
		this.httpHosts = httpHosts;
	}

	public List<Map<String, Object>> getHttpHosts() {
		return httpHosts;
	}

	public Map<String, Object> getRequestConfig() {
		return requestConfig;
	}

	public void setRequestConfig(Map<String, Object> requestConfig) {
		this.requestConfig = requestConfig;
	}

	public HttpClientConfig getHttpClientConfig() {
		return httpClientConfig;
	}

	public void setHttpClientConfig(HttpClientConfig httpClientConfig) {
		this.httpClientConfig = httpClientConfig;
	}

	public static class HttpClientConfig {
		private Map<String, Object> ioReactorConfig = Maps.newHashMap();
		private Map<String, Object> threadFactoryConfig = Maps.newHashMap();

		public Map<String, Object> getIoReactorConfig() {
			return ioReactorConfig;
		}

		public void setIoReactorConfig(Map<String, Object> ioReactorConfig) {
			this.ioReactorConfig = ioReactorConfig;
		}

		public Map<String, Object> getThreadFactoryConfig() {
			return threadFactoryConfig;
		}

		public void setThreadFactoryConfig(Map<String, Object> threadFactoryConfig) {
			this.threadFactoryConfig = threadFactoryConfig;
		}

	}

}