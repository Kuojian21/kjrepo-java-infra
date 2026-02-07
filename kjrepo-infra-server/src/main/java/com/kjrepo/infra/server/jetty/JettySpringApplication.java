package com.kjrepo.infra.server.jetty;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class JettySpringApplication {
    
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        
        // 创建 Servlet 上下文
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        
        // 创建 Spring 应用上下文
        AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
        springContext.register(SpringMvcConfig.class);
        
        // 添加 Spring ContextLoaderListener
        context.addEventListener(new ContextLoaderListener(springContext));
        
        // 注册 Spring DispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet(springContext);
        ServletHolder servletHolder = new ServletHolder("dispatcher", dispatcherServlet);
        servletHolder.setInitOrder(1);
        context.addServlet(servletHolder, "/*");
        
        server.setHandler(context);
        
        // 启动服务器
        server.start();
        System.out.println("Jetty + Spring MVC 启动在: http://localhost:8080");
        server.join();
    }
}