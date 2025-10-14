package com.kjrepo.infra.trace.client;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;

import com.kjrepo.infra.trace.utils.TraceIDUtils;

public class TraceHttpResponseInterceptor implements HttpResponseInterceptor {

	@Override
	public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
		Object obj = context.getAttribute(TraceIDUtils.ID_REQUEST);
		TraceIDUtils.set(obj == null ? null : obj.toString());
	}

}
