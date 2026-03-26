package com.kjrepo.infra.network.http.sync;

import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.annimon.stream.Optional;
import com.annimon.stream.function.ThrowableConsumer;
import com.annimon.stream.function.ThrowableFunction;
import com.kjrepo.infra.executor.lazy.LazyExecutor;
import com.kjrepo.infra.network.http.utils.KhttpUtils;
import com.kjrepo.infra.text.json.JsonUtils;

public class KhttpClient extends LazyExecutor<CloseableHttpClient, KhttpClientInfo> {

	public static final KhttpClient DEFAULT = new KhttpClient(new KhttpClientInfo());

	public KhttpClient(KhttpClientInfo info) {
		super(info, () -> KhttpUtils.client(info));
	}

	public final String call(String url, String method) throws IOException {
		return call(url, method, (List<Header>) null, String.class);
	}

	public final String call(String url, String method, List<Header> headers) throws IOException {
		return call(url, method, headers, String.class);
	}

	public final <T> T call(String url, String method, Class<T> clazz) throws IOException {
		return call(url, method, null, clazz);
	}

	@SuppressWarnings("unchecked")
	public final <T> T call(String url, String method, List<Header> headers, Class<T> clazz) throws IOException {
		RequestBuilder builder = RequestBuilder.create(Optional.ofNullable(method).orElse(HttpGet.METHOD_NAME))
				.setUri(url);
		Optional.ofNullable(headers).ifPresent(m -> m.forEach(builder::addHeader));
		return (T) apply(builder, null, response -> {
			String str = EntityUtils.toString(response.getEntity());
			if (clazz == String.class) {
				return str;
			}
			return JsonUtils.fromJson(str, clazz);
		});
	}

	public final <T, X extends Throwable> void accept(RequestBuilder request, EntityBuilder entity,
			ThrowableConsumer<HttpResponse, X> rspHandler) throws IOException, X {
		apply(entity != null ? request.setEntity(entity.build()).build() : request.build(), null, rsp -> {
			rspHandler.accept(rsp);
			return null;
		});
	}

	public final <T, X extends Throwable> T apply(RequestBuilder request, EntityBuilder entity,
			ThrowableFunction<HttpResponse, T, X> rspHandler) throws IOException, X {
		return apply(entity != null ? request.setEntity(entity.build()).build() : request.build(), null, rspHandler);
	}

	public final <T, X extends Throwable> T apply(HttpUriRequest request, HttpContext context,
			ThrowableFunction<HttpResponse, T, X> rspHandler) throws IOException, X {
		HttpResponse response = null;
		try {
			response = execute(bean -> {
				return bean.execute(request, context != null ? context : HttpClientContext.create());
			}, request.getURI().getHost());
			return rspHandler.apply(response);
		} finally {
			HttpClientUtils.closeQuietly(response);
		}
	}

}
