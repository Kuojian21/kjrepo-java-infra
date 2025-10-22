package com.kjrepo.infra.network.http;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;

public class KhttpAsyncClientFutureCallback<T, E extends Exception> implements FutureCallback<HttpResponse> {

	private final Logger logger = LoggerUtils.logger(getClass());

	@Override
	public void completed(HttpResponse response) {

	}

	@Override
	public void failed(Exception ex) {
		logger.error("", ex);
	}

	@Override
	public void cancelled() {
		logger.warn("cancelled!!!");
	}

}
