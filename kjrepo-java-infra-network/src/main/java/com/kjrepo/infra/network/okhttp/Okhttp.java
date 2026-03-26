package com.kjrepo.infra.network.okhttp;

import com.kjrepo.infra.executor.lazy.LazyExecutor;

import okhttp3.OkHttpClient;

public class Okhttp extends LazyExecutor<OkHttpClient, OkhttpInfo> {

	public Okhttp(OkhttpInfo info) {
		super(info, () -> OkhttpUtils.okhttp(info));
	}
}
