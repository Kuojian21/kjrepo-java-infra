package com.kjrepo.infra.server.legacy;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.io.File;

public class JettyHttpToHttpsRedirect {

	public static void main(String[] args) throws Exception {
		// 1. 创建 Server 实例
		Server server = new Server();

		// 2. 配置 HTTP 连接器 (监听 8080 端口)
		HttpConfiguration httpConfig = new HttpConfiguration();
		// 关键配置：设置 HTTPS 的端口和协议，这样 Jetty 就知道重定向的目标
		httpConfig.setSecurePort(8443);
		httpConfig.setSecureScheme("https");

		// HTTP 连接器
		ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
		httpConnector.setPort(8080);

		// 3. 配置 HTTPS 连接器 (监听 8443 端口，需要证书)
		HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
		httpsConfig.addCustomizer(new SecureRequestCustomizer()); // 启用安全请求定制

		// 使用一个简单的自签名证书配置 (生产环境请替换为真实证书)
		String jettyBase = System.getProperty("java.io.tmpdir");
		String keystorePath = new File(jettyBase, "keystore").getAbsolutePath();
		// 注意: 你需要先生成一个 keystore 文件，或者使用下面的代码动态生成一个（示例中省略了动态生成的细节）
		// 这里假设你已经有一个 keystore 文件在指定路径

		SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
		sslContextFactory.setKeyStorePath(keystorePath); // 请替换为你的 keystore 路径
		sslContextFactory.setKeyStorePassword("your_keystore_password"); // 请替换为你的密码

		ServerConnector httpsConnector = new ServerConnector(server,
				new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(httpsConfig));
		httpsConnector.setPort(8443);

		// 将两个连接器添加到服务器
		server.setConnectors(new Connector[] { httpConnector, httpsConnector });

		// 4. 创建 Web 应用上下文 (这里以一个简单的 Web 应用为例)
		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		// 设置你的 web 应用路径，例如 "src/main/webapp" 或一个临时的空目录
		webAppContext.setResourceBase(new File("src/main/webapp").getAbsolutePath());
		webAppContext.setParentLoaderPriority(true);

		// 5. 添加安全处理器，实现 HTTP -> HTTPS 自动跳转
		ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();

		// 5.1 创建约束：要求数据通过机密通道传输（即 HTTPS）
		Constraint constraint = new Constraint();
		constraint.setName("confidential");
		constraint.setDataConstraint(Constraint.DC_CONFIDENTIAL); // 核心设置！

		// 5.2 创建约束映射：将约束应用到所有 URL 路径 (/*)
		ConstraintMapping mapping = new ConstraintMapping();
		mapping.setConstraint(constraint);
		mapping.setPathSpec("/*");

		// 5.3 将映射添加到安全处理器，并设置为 Web 应用的安全处理器
		securityHandler.addConstraintMapping(mapping);
		securityHandler.setHandler(webAppContext);

		// 6. 将安全处理器设置为服务器的处理器
		server.setHandler(securityHandler);

		// 7. 启动服务器
		server.start();
		System.out.println("Jetty started. HTTP on port 8080, HTTPS on port 8443.");
		System.out.println("访问 http://localhost:8080 将自动重定向到 https://localhost:8443");

		server.join();
	}
}