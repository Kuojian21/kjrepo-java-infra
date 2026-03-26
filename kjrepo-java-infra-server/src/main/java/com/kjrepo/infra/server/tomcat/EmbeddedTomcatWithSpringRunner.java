package com.kjrepo.infra.server.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class EmbeddedTomcatWithSpringRunner {

	public static void main(String[] args) throws LifecycleException, IOException {
		// 1. 创建 Tomcat 实例
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(8080);

		// 2. 设置工作目录（重要：避免临时目录被清理）
		File baseDir = Files.createTempDirectory("tomcat-base-").toFile();
		baseDir.deleteOnExit();
		tomcat.setBaseDir(baseDir.getAbsolutePath());

		// 3. 创建 Context（Web 应用上下文）
		String contextPath = "";
		// 创建一个临时目录作为 docBase（因为我们没有静态文件）
		File docBaseDir = Files.createTempDirectory("webapp-docbase-").toFile();
		docBaseDir.deleteOnExit();

		Context context = tomcat.addContext(contextPath, docBaseDir.getAbsolutePath());

		// 4. 关键步骤：添加 ServletContainerInitializer
		// 这里将触发 SpringServletContainerInitializer 的 onStartup 方法
		context.addServletContainerInitializer(new org.springframework.web.SpringServletContainerInitializer(), null // 可以传递感兴趣的类集合，这里用
																														// null
																														// 表示扫描所有
		);

		// 5. 启动 Tomcat
		tomcat.start();
		System.out.println("Embedded Tomcat with Spring MVC started at http://localhost:8080/hello");

		// 6. 等待服务器关闭
		tomcat.getServer().await();
	}
}