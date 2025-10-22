package com.kjrepo.infra.network.http;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.text.json.ConfigUtils;
import com.kjrepo.infra.trace.client.TraceHttpRequestInterceptor;
import com.kjrepo.infra.trace.client.TraceHttpResponseInterceptor;

public class KhttpUtils {

	private static final Logger logger = LoggerUtils.logger(KhttpUtils.class);

	public static CloseableHttpClient client(KhttpClientInfo info) {
		PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
		manager.setDefaultMaxPerRoute(info.getDefaultMaxPerRoute());
		manager.setMaxTotal(info.getMaxTotal());
		manager.setDefaultSocketConfig(ConfigUtils
				.config(SocketConfig.custom().setTcpNoDelay(true).setSoTimeout((int) TimeUnit.SECONDS.toMillis(1)),
						info.getSocketConfig())
				.build());
		HttpClientBuilder builder = HttpClientBuilder.create().setConnectionManager(manager)
				.setDefaultRequestConfig(ConfigUtils.config(
						RequestConfig.custom().setConnectTimeout((int) TimeUnit.SECONDS.toMillis(1))
								.setConnectionRequestTimeout((int) TimeUnit.SECONDS.toMillis(1)),
						info.getRequestConfig()).build())
				.addInterceptorFirst(new TraceHttpRequestInterceptor());
		return builder.build();
	}

	public static CloseableHttpAsyncClient client(KhttpAsyncClientInfo info) {
		try {
			PoolingNHttpClientConnectionManager manager = new PoolingNHttpClientConnectionManager(
					new DefaultConnectingIOReactor(IOReactorConfig.DEFAULT, Executors.defaultThreadFactory()));
			manager.setDefaultMaxPerRoute(info.getDefaultMaxPerRoute());
			manager.setMaxTotal(info.getMaxTotal());
			CloseableHttpAsyncClient client = HttpAsyncClientBuilder.create().setConnectionManager(manager)
					.addInterceptorFirst(new TraceHttpRequestInterceptor())
					.addInterceptorFirst(new TraceHttpResponseInterceptor()).build();
			client.start();
			return client;
		} catch (IOReactorException e) {
			logger.info("", e);
			throw new RuntimeException(e);
		}
	}

	public static String toString(HttpEntity entity) {
		try {
			return EntityUtils.toString(entity, "utf-8");
		} catch (ParseException | IOException e) {
			logger.error("", e);
			throw new RuntimeException(e);
		}
	}

	public static Future<HttpResponse> call(RequestBuilder request, EntityBuilder entity, HttpClientContext context,
			FutureCallback<HttpResponse> callback) {
		return KhttpAsyncClient.DEFAULT.call(request, entity, context, callback);
	}

}
