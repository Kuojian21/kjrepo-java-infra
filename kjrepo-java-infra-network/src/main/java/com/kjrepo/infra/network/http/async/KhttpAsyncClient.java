package com.kjrepo.infra.network.http.async;

import java.util.List;
import java.util.concurrent.Future;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;

import com.annimon.stream.Optional;
import com.kjrepo.infra.executor.lazy.LazyExecutor;
import com.kjrepo.infra.network.http.utils.KhttpUtils;
import com.kjrepo.infra.text.json.JsonUtils;
import com.kjrepo.infra.thread.future.MapperFuture;

public class KhttpAsyncClient extends LazyExecutor<CloseableHttpAsyncClient, KhttpAsyncClientInfo> {

	public static final KhttpAsyncClient DEFAULT = new KhttpAsyncClient(new KhttpAsyncClientInfo());

	public KhttpAsyncClient(KhttpAsyncClientInfo info) {
		super(info, () -> KhttpUtils.client(info));
	}

	public final Future<String> call(String url, String method) {
		return call(url, method, (List<Header>) null);
	}

	public final Future<String> call(String url, String method, List<Header> headers) {
		return call(url, method, headers, String.class);
	}

	public final <T> Future<T> call(String url, String method, Class<T> clazz) {
		return call(url, method, null, clazz);
	}

	@SuppressWarnings("unchecked")
	public final <T> Future<T> call(String url, String method, List<Header> headers, Class<T> clazz) {
		RequestBuilder builder = RequestBuilder.create(Optional.ofNullable(method).orElse(HttpGet.METHOD_NAME))
				.setUri(url);
		Optional.ofNullable(headers).ifPresent(m -> m.forEach(builder::addHeader));
		Future<HttpResponse> future = call(builder, DefaultFutureCallback.DEFAULT);
		return MapperFuture.wrap(future, response -> {
			try {
				String json = EntityUtils.toString(response.getEntity());
				if (clazz == String.class) {
					return (T) json;
				}
				return JsonUtils.fromJson(json, clazz);
			} finally {
				HttpClientUtils.closeQuietly(response);
			}
		});
	}

	public final Future<HttpResponse> call(RequestBuilder request) {
		return call(request, DefaultFutureCallback.DEFAULT);
	}

	public final Future<HttpResponse> call(RequestBuilder request, FutureCallback<HttpResponse> callback) {
		return call(request.build(), null, callback);
	}

	public final Future<HttpResponse> call(HttpUriRequest request, HttpClientContext context,
			FutureCallback<HttpResponse> callback) {
		return execute(bean -> {
			return bean.execute(request, context != null ? context : HttpClientContext.create(), callback);
		});
	}

}
