package com.kjrepo.infra.network.okhttp;

import com.kjrepo.infra.common.executor.LazyInfoExecutor;

import okhttp3.OkHttpClient;

public class Okhttp extends LazyInfoExecutor<OkHttpClient, OkhttpInfo> {

	public Okhttp(OkhttpInfo info) {
		super(info, () -> OkhttpUtils.okhttp(info));
	}
}
