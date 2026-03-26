package com.kjrepo.infra.network.okhttp;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.annimon.stream.function.ThrowableFunction;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;

public class OkhttpAsyncCallback<T> implements Callback {

	private final ThrowableFunction<Response, T, Exception> mapper;
	private volatile T value;
	private volatile Exception exception;

	private FutureTask<T> future = new FutureTask<>(() -> {
		if (exception != null) {
			throw exception;
		} else {
			return this.value;
		}
	});

	public OkhttpAsyncCallback(ThrowableFunction<Response, T, Exception> mapper) {
		super();
		this.mapper = mapper;
	}

	@Override
	public void onFailure(Call call, IOException e) {
		this.exception = e;
		future.run();
	}

	@Override
	public void onResponse(Call call, Response response) throws IOException {
		try {
			this.value = this.mapper.apply(response);
			future.run();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			Util.closeQuietly(response);
		}
	}

	public Future<T> getFuture() {
		return future;
	}

}
