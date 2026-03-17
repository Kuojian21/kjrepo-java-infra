package com.kjrepo.infra.server.jetty;

import java.util.Map;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import jakarta.servlet.Servlet;

public class JettyStandaloneServer extends JettyServer {

	private final String contextPath;
	private final Map<String, Servlet> servlets;

	public JettyStandaloneServer(String contextPath, Map<String, Servlet> servlets) {
		this.contextPath = contextPath;
		this.servlets = servlets;
	}

	@Override
	public ServletContextHandler context() {
		ServletContextHandler handler = new ServletContextHandler();
		handler.setContextPath(this.contextPath);
		servlets.forEach((path, servlet) -> {
			handler.addServlet(new ServletHolder(servlet), path);
		});
		return handler;
	}

}
