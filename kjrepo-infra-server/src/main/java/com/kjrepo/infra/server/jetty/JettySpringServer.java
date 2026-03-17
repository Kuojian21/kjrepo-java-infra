package com.kjrepo.infra.server.jetty;

import java.util.List;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.annimon.stream.Stream;

public class JettySpringServer extends JettyServer {

	private final String contextPath;
	private final List<Class<?>> components;

	public JettySpringServer(String contextPath, List<Class<?>> components) {
		this.contextPath = contextPath;
		this.components = components;
	}

	public ServletContextHandler context() {
		ServletContextHandler handler = new ServletContextHandler();
		handler.setContextPath(this.contextPath);

		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		Stream.of(this.components).forEach(context::register);
		handler.addEventListener(new ContextLoaderListener(context));

		DispatcherServlet dispatcher = new DispatcherServlet(context);
		ServletHolder servlet = new ServletHolder("dispatcher", dispatcher);
		servlet.setInitOrder(1);
		handler.addServlet(servlet, "/*");
		return handler;
	}

}
