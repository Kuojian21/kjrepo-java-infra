package com.kjrepo.infra.server.legacy;

import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import java.io.File;
import java.net.URL;

public class JettyWebAppLauncher {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		// 创建 WebApp 上下文
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");

		// 设置 WAR 文件路径或 WebApp 目录
		String webappDir = findWebappDirectory();
		webapp.setWar(webappDir);

		// 配置 WebApp
		webapp.setExtractWAR(true);
		webapp.setCopyWebDir(true);
		webapp.setCopyWebInf(true);

		server.setHandler(webapp);

		// 启动服务器
		server.start();
		System.out.println("WebApp 启动在: http://localhost:8080");
		server.join();
	}

	private static String findWebappDirectory() {
		// 查找 webapp 目录
		File webappDir = new File("src/main/webapp");
		if (webappDir.exists()) {
			return webappDir.getAbsolutePath();
		}

		// 如果在 classpath 中
		URL resource = JettyWebAppLauncher.class.getClassLoader().getResource("webapp");
		if (resource != null) {
			return resource.getPath();
		}

		throw new RuntimeException("未找到 webapp 目录");
	}
}