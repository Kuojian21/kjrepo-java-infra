package com.kjrepo.infra.trace.okhttp;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.kjrepo.infra.trace.utils.TraceIDUtils;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TraceInterceptor implements Interceptor {

	@Override
	public Response intercept(Chain chain) throws IOException {
		String traceId = TraceIDUtils.get();
		Request request = chain.request();
		if (!StringUtils.isEmpty(traceId)) {
			request = request.newBuilder().addHeader(TraceIDUtils.ID_REQUEST, traceId).build();
		}
		return chain.proceed(request);
	}

}
