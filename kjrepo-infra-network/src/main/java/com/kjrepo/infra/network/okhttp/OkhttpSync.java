package com.kjrepo.infra.network.okhttp;

import java.util.List;

import com.annimon.stream.Optional;
import com.annimon.stream.function.ThrowableFunction;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.info.Pair;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;

public class OkhttpSync extends Okhttp {

	public static final OkhttpSync DEFAULT = new OkhttpSync(new OkhttpInfo());

	public OkhttpSync(OkhttpInfo info) {
		super(info);
	}

	public String json(String url, String method, List<Pair<String, String>> headers, String json) {
		return call(url, method, headers, RequestBody.create(MediaType.get("application/json; charset=utf-8"), json));
	}

	public String call(String url, String method, List<Pair<String, String>> headers, RequestBody body) {
		Request.Builder builder = new Request.Builder().url(url);
		builder.method(method, body);
		Optional.ofNullable(headers).orElseGet(() -> Lists.newArrayList()).forEach(p -> {
			builder.addHeader(p.getKey(), p.getValue());
		});
		return call(builder, response -> response.body().string());
	}

	public final <T> T call(Request.Builder builder, ThrowableFunction<Response, T, Throwable> handler) {
		Response response = null;
		try {
			response = execute(client -> {
				return client.newCall(builder.build()).execute();
			});
			return handler.apply(response);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			if (response != null) {
				Util.closeQuietly(response);
			}
		}
	}

}
