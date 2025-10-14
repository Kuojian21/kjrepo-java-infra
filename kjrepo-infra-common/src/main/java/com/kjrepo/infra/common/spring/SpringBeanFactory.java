package com.kjrepo.infra.common.spring;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Service;

@Service
public final class SpringBeanFactory {

	public static <T> T getBean(Class<T> clazz) {
		init();
		return applicationContext.getBean(clazz);
	}

	public static <T> T getBean(Class<T> clazz, Object... args) {
		init();
		return applicationContext.getBean(clazz, args);
	}

	public static <T> T getBean(String beanName, Class<T> tClass) {
		init();
		return applicationContext.getBean(beanName, tClass);
	}

	public static <T> Map<String, T> getBeans(Class<T> type) {
		init();
		return BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, type);
	}

	private static volatile GenericApplicationContext applicationContext;

	private static void init() {
		if (applicationContext == null) {
			synchronized (SpringBeanFactory.class) {
				if (applicationContext == null) {
					try {
						applicationContext = new GenericApplicationContext(
								new ClassPathXmlApplicationContext("classpath*:spring/*.xml"));
						applicationContext.refresh();
					} catch (RuntimeException e) {
						throw e;
					}
				}
			}
		}
	}

	public SpringBeanFactory() {
	}

	@Autowired
	private void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		synchronized (SpringBeanFactory.class) {
			if (SpringBeanFactory.applicationContext == null) {
				if (applicationContext instanceof GenericApplicationContext) {
					SpringBeanFactory.applicationContext = (GenericApplicationContext) applicationContext;
				} else {
					SpringBeanFactory.applicationContext = new GenericApplicationContext(applicationContext);
					SpringBeanFactory.applicationContext.refresh();
				}
			} else {
				throw new RuntimeException("The applicationContext has already been setted!!!");
			}
		}
	}
}
