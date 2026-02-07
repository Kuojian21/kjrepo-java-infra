package com.kjrepo.infra.network.http;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.annimon.stream.Optional;
import com.annimon.stream.function.ThrowableFunction;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.executor.LazyInfoExecutor;
import com.kjrepo.infra.common.info.Pair;

public class KhttpClient extends LazyInfoExecutor<CloseableHttpClient, KhttpClientInfo> {

	public static final KhttpClient DEFAULT = new KhttpClient(new KhttpClientInfo());

	public KhttpClient(KhttpClientInfo info) {
		super(info, () -> KhttpUtils.client(info));
	}

	public final String call(String url, String method) {
		return call(url, method, null, null);
	}

	public final String call(String url, String method, List<Pair<String, String>> headers,
			List<Pair<String, String>> params) {
		RequestBuilder builder = RequestBuilder
				.create(Optional.ofNullable(method).orElseGet(() -> HttpPost.METHOD_NAME)).setUri(url);
		Optional.ofNullable(headers).orElseGet(() -> Lists.newArrayList()).forEach(p -> {
			builder.addHeader(p.getKey(), p.getValue());
		});
		Optional.ofNullable(params).orElseGet(() -> Lists.newArrayList()).forEach(p -> {
			builder.addParameter(p.getKey(), p.getValue());
		});
		return call(builder, null, HttpClientContext.create(), response -> EntityUtils.toString(response.getEntity()));
	}

	public final <T> T call(RequestBuilder request, EntityBuilder entity,
			ThrowableFunction<HttpResponse, T, Exception> rspFunc) {
		return call(request, entity, null, rspFunc);
	}

	public final <T> T call(RequestBuilder request, EntityBuilder entity, HttpClientContext context,
			ThrowableFunction<HttpResponse, T, Exception> rspFunc) {
		return call(entity != null ? request.setEntity(entity.build()).build() : request.build(), context, rspFunc);
	}

	public final <T> T call(HttpUriRequest request, ThrowableFunction<HttpResponse, T, Exception> rspFunc) {
		return call(request, null, rspFunc);
	}

	public final <T> T call(HttpUriRequest request, HttpContext context,
			ThrowableFunction<HttpResponse, T, Exception> rspFunc) {
		HttpResponse response = null;
		try {
			response = execute(bean -> {
				return bean.execute(request, context != null ? context : HttpClientContext.create());
			});
			return rspFunc.apply(response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			HttpClientUtils.closeQuietly(response);
		}
	}
}
