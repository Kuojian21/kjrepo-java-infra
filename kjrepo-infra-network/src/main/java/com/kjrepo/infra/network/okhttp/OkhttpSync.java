package com.kjrepo.infra.network.okhttp;

import java.io.IOException;
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

	public String json(String url, String method, List<Pair<String, String>> headers, String json) throws IOException {
		return call(url, method, headers, RequestBody.create(MediaType.get("application/json; charset=utf-8"), json));
	}

	public <X extends Throwable> String call(String url, String method, List<Pair<String, String>> headers,
			RequestBody body) throws IOException {
		Request.Builder builder = new Request.Builder().url(url);
		builder.method(method, body);
		Optional.ofNullable(headers).orElseGet(() -> Lists.newArrayList()).forEach(p -> {
			builder.addHeader(p.getKey(), p.getValue());
		});
		return call(builder, response -> response.body().string());
	}

	@SuppressWarnings("unchecked")
	public final <T, X extends Throwable> T call(Request.Builder builder, ThrowableFunction<Response, T, X> handler)
			throws X {
		Response response = null;
		try {
			Request request = builder.build();
			response = execute(client -> {
				return client.newCall(request).execute();
			}, request.url().host());
			return handler.apply(response);
		} catch (Throwable e) {
			throw (X) e;
		} finally {
			if (response != null) {
				Util.closeQuietly(response);
			}
		}
	}

}
