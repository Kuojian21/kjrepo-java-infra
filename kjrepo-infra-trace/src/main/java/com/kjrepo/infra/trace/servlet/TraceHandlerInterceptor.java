package com.kjrepo.infra.trace.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kjrepo.infra.trace.utils.TraceIDUtils;

public class TraceHandlerInterceptor extends HandlerInterceptorAdapter {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		TraceIDUtils.generate(request.getHeader(TraceIDUtils.ID_REQUEST));
		return true;
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception ex) throws Exception {
		TraceIDUtils.clear();
	}
}
