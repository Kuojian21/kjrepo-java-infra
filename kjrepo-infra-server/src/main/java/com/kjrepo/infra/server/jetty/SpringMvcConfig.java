package com.kjrepo.infra.server.jetty;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
// Spring 配置类
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan("com.kjrepo.infra.web.jetty")
public class SpringMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/WEB-INF/views/", ".jsp");
        registry.freeMarker();
    }
    
    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        
        // 设置模板加载路径（相对于classpath）
        configurer.setTemplateLoaderPath("classpath:/templates/");
        
        // 设置默认编码
        configurer.setDefaultEncoding("UTF-8");
        
        // 配置FreeMarker设置
        Properties settings = new Properties();
        settings.setProperty("template_update_delay", "5"); // 5秒更新检查
        settings.setProperty("default_encoding", "UTF-8");
        settings.setProperty("url_escaping_charset", "UTF-8");
        settings.setProperty("locale", "zh_CN");
        settings.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
        settings.setProperty("date_format", "yyyy-MM-dd");
        settings.setProperty("time_format", "HH:mm:ss");
        settings.setProperty("number_format", "0.######");
        settings.setProperty("whitespace_stripping", "true");
        settings.setProperty("classic_compatible", "true");
        settings.setProperty("template_exception_handler", "html_debug");
        
        configurer.setFreemarkerSettings(settings);
        
        return configurer;
    }
    
    @Bean
    public FreeMarkerViewResolver freeMarkerViewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setCache(true);
        resolver.setPrefix("");
        resolver.setSuffix(".ftl");
        resolver.setContentType("text/html;charset=UTF-8");
        resolver.setExposeSpringMacroHelpers(true);
        resolver.setExposeRequestAttributes(true);
        resolver.setExposeSessionAttributes(true);
        resolver.setExposePathVariables(true);
        resolver.setRequestContextAttribute("request");
        resolver.setOrder(0); // 设置优先级
        return resolver;
    }
}