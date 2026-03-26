package com.kjrepo.infra.server.tomcat;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

public class EmbeddedTomcatExample {

	public static void main(String[] args) throws LifecycleException {
		// 1. 创建 Tomcat 实例
		Tomcat tomcat = new Tomcat();

		// 2. 设置端口号（默认就是8080，这里显式设置）
		tomcat.setPort(8080);

		// 3. 设置文档基目录（用于静态资源，非必需但推荐）
		// 如果不存在，Tomcat 会创建一个临时目录
		String docBase = new File(".").getAbsolutePath();

		// 4. 添加一个 Web 应用程序上下文（Context）
		// 参数：上下文路径，文档基目录
		// "" 表示这是根应用 (ROOT)
		tomcat.addWebapp("", docBase);

		// **或者，如果你不想使用标准的 webapp 结构，可以编程式添加 Servlet**
		// addProgrammaticServlet(tomcat);

		// 5. 启动 Tomcat 服务器并等待
		tomcat.start();
		System.out.println("Tomcat 已启动，访问地址: http://localhost:" + tomcat.getConnector().getPort());
		tomcat.getServer().await(); // 保持主线程不退出，等待请求
	}

	/**
	 * 编程式添加 Servlet 的示例方法
	 */
	@SuppressWarnings("unused")
	private static void addProgrammaticServlet(Tomcat tomcat) {
		// 创建一个上下文
		var context = tomcat.addContext("", null);

		// 创建并添加一个 Servlet
		String servletName = "HelloServlet";
		String urlPattern = "/hello";
		tomcat.addServlet("", servletName, new HttpServlet() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void doGet(HttpServletRequest req, HttpServletResponse resp)
					throws ServletException, IOException {
				Writer writer = resp.getWriter();
				writer.write("<!DOCTYPE html><html><body>");
				writer.write("<h1>Hello, Embedded Tomcat!</h1>");
				writer.write("<p>This is a programmatically added servlet.</p>");
				writer.write("</body></html>");
				writer.flush();
			}
		});

		// 将 Servlet 映射到 URL 模式
		context.addServletMappingDecoded(urlPattern, servletName);
	}
}