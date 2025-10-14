package com.kjrepo.infra.network.http;

import java.util.List;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;

import com.annimon.stream.Optional;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.executor.LazyInfoExecutor;
import com.kjrepo.infra.common.info.Pair;
import com.kjrepo.infra.thread.future.MapperFuture;

public class KhttpAsyncClient extends LazyInfoExecutor<CloseableHttpAsyncClient, KhttpAsyncClientInfo> {

	public static final KhttpAsyncClient DEFAULT = new KhttpAsyncClient(new KhttpAsyncClientInfo());

	public KhttpAsyncClient(KhttpAsyncClientInfo info) {
		super(info, () -> KhttpUtils.client(info));
	}

	public Future<String> call(String url, String method, List<Pair<String, String>> headers,
			List<Pair<String, String>> params) {
		RequestBuilder builder = RequestBuilder
				.create(Optional.ofNullable(method).orElseGet(() -> HttpPost.METHOD_NAME)).setUri(url);
		Optional.ofNullable(headers).orElseGet(() -> Lists.newArrayList()).forEach(p -> {
			builder.addHeader(p.getKey(), p.getValue());
		});
		Optional.ofNullable(params).orElseGet(() -> Lists.newArrayList()).forEach(p -> {
			builder.addParameter(p.getKey(), p.getValue());
		});
		Future<HttpResponse> future = call(builder, null, HttpClientContext.create(),
				new KhttpAsyncClientFutureCallback<>());
		return MapperFuture.wrap(future, response -> {
			try {
				return EntityUtils.toString(response.getEntity());
			} finally {
				HttpClientUtils.closeQuietly(response);
			}
		});

	}

	public final Future<HttpResponse> call(RequestBuilder request, EntityBuilder entity, HttpClientContext context,
			FutureCallback<HttpResponse> callback) {
		if (entity != null) {
			request.setEntity(entity.build());
		}
		return execute(bean -> {
			return bean.execute(request.build(), context, callback);
		});
	}

}
