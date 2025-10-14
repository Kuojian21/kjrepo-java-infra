package com.kjrepo.infra.trace.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.kjrepo.infra.trace.utils.TraceIDUtils;

public class TraceFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		TraceIDUtils.generate(((HttpServletRequest) request).getHeader(TraceIDUtils.ID_REQUEST));
		chain.doFilter(request, response);
		TraceIDUtils.clear();
	}

}
