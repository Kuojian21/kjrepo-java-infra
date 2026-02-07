package com.kjrepo.infra.server.jetty;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class JettyHttpsServer {

	public static void main(String[] args) throws Exception {
		Server server = new Server();

		// 配置 SSL
		SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
		sslContextFactory.setKeyStorePath("/path/to/keystore.jks");
		sslContextFactory.setKeyStorePassword("password");
		sslContextFactory.setKeyManagerPassword("password");

		// HTTPS 连接器
		ServerConnector sslConnector = new ServerConnector(server,
				new SslConnectionFactory(sslContextFactory, "http/1.1"),
				new HttpConnectionFactory(new HttpConfiguration()));
		sslConnector.setPort(8443);

		// HTTP 连接器（重定向到 HTTPS）
		ServerConnector httpConnector = new ServerConnector(server);
		httpConnector.setPort(8080);

		server.addConnector(sslConnector);
		server.addConnector(httpConnector);

		// 设置处理器
		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/");
		context.addServlet(HelloServlet.class, "/hello");
		server.setHandler(context);

		server.start();
		System.out.println("HTTPS 服务器启动: https://localhost:8443");
		server.join();
	}
}