package com.kjrepo.infra.server.jetty;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;

public class JettyStandaloneApplication {
    
    public static void main(String[] args) throws Exception {
        // 创建 Jetty 服务器实例，端口 8080
        Server server = new Server(8080);
        
        // 创建 Servlet 上下文处理器
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        
        // 注册 Servlet
        ServletHolder servletHolder = new ServletHolder(new HelloServlet());
        context.addServlet(servletHolder, "/hello");
        
        // 启动服务器
        server.start();
        System.out.println("Jetty 服务器启动在: http://localhost:8080");
        
        // 等待服务器停止
        server.join();
    }
}