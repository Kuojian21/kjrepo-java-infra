package com.kjrepo.infra.storage.es;

import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClient.FailureListener;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;
import org.slf4j.Logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.text.json.ConfigUtils;
import com.kjrepo.infra.text.json.JsonUtils;

public class ElasticsearchUtils {

	private static final Logger LOGGER = LoggerUtils.logger(ElasticsearchUtils.class);

	public static RestClient client(ElasticsearchInfo info, FailureListener failureListener) {
		info.getHttpClientConfig().getThreadFactoryConfig().putIfAbsent("daemon", true);
		info.getHttpClientConfig().getThreadFactoryConfig().putIfAbsent("nameFormat", "elasticsearch-rest-%d");
		return client(info.getHttpHosts().stream().map(ElasticsearchUtils::toHttpHost).toArray(i -> new HttpHost[i]),
				builder -> ConfigUtils.config(builder, info.getRequestConfig()),
				builder -> builder.setDefaultIOReactorConfig(ConfigUtils
						.config(IOReactorConfig.custom(), info.getHttpClientConfig().getIoReactorConfig()).build())
						.setThreadFactory(ConfigUtils
								.config(new ThreadFactoryBuilder(), info.getHttpClientConfig().getThreadFactoryConfig())
								.build()),
				failureListener);
	}

	public static RestClient client(HttpHost[] hosts, RequestConfigCallback requestBuilder,
			HttpClientConfigCallback configBuilder, FailureListener failureListener) {
		if (failureListener == null) {
			failureListener = new FailureListener() {
				public void onFailure(Node node) {
					LOGGER.error("failure node: {} ", JsonUtils.toJson(node));
				}
			};
		}
		return RestClient.builder(hosts).setRequestConfigCallback(requestBuilder)
				.setHttpClientConfigCallback(configBuilder).setFailureListener(failureListener).build();
	}

	public static HttpHost toHttpHost(Map<String, Object> map) {
		return new HttpHost((String) map.get("host"), (Integer) map.get("port"), (String) map.get("scheme"));
	}

}
