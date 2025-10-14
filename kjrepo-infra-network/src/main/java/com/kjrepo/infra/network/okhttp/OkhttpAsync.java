package com.kjrepo.infra.network.okhttp;

import java.util.List;
import java.util.concurrent.Future;

import com.annimon.stream.Optional;
import com.annimon.stream.function.ThrowableFunction;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.info.Pair;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpAsync extends Okhttp {

	public static final OkhttpAsync DEFAULT = new OkhttpAsync(new OkhttpInfo());

	public OkhttpAsync(OkhttpInfo info) {
		super(info);
	}

	public Future<String> json(String url, String method, List<Pair<String, String>> headers, String json) {
		return call(url, method, headers, RequestBody.create(MediaType.get("application/json; charset=utf-8"), json));
	}

	public Future<String> call(String url, String method, List<Pair<String, String>> headers, RequestBody body) {
		Request.Builder builder = new Request.Builder().url(url);
		builder.method(method, body);
		Optional.ofNullable(headers).orElseGet(() -> Lists.newArrayList()).forEach(p -> {
			builder.addHeader(p.getKey(), p.getValue());
		});
		return call(builder, response -> response.body().string());
	}

	public final <T> Future<T> call(Request.Builder builder, ThrowableFunction<Response, T, Exception> handler) {
		OkhttpAsyncCallback<T> callback = new OkhttpAsyncCallback<T>(handler);
		execute(client -> {
			client.newCall(builder.build()).enqueue(callback);
		});
		return callback.getFuture();
	}

}
