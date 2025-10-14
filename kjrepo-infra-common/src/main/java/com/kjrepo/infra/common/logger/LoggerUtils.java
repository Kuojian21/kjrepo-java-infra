package com.kjrepo.infra.common.logger;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.annimon.stream.Stream;

public class LoggerUtils {

	public static Logger logger() {
		StackTraceElement element = Thread.currentThread().getStackTrace()[2];
		if (!"<clinit>".equals(element.getMethodName()) && !"<init>".equals(element.getMethodName())) {
			factory.getLogger(LoggerUtils.class).error(
					"please invoke this method in <clinit> or <init> method!!! location:{}.{}:{}",
					element.getClassName(), element.getMethodName(), element.getLineNumber());
		}
		return factory.getLogger(element.getClassName());
	}

	public static Logger logger(Class<?> clazz) {
		return logger(clazz.getName());
	}

	public static Logger logger(String name) {
		return factory.getLogger(wrap(name));
	}

	public static String wrap(String name) {
		return "TRUE".equalsIgnoreCase(System.getProperty(KLOGGER_SYNC, "")) ? sync(name) : name;
	}

	public static String sync(String name) {
		return "sync." + name;
	}

	private static final String KLOGGER_FACTORY = "KLoggerFactory";
	private static final String KLOGGER_SYNC = "KLoggerSync";
	private static final IKLoggerFactory factory = factory();
	public static final Logger logger = factory.getLogger(LoggerUtils.class);

	private static final IKLoggerFactory factory() {
		String factoryClazz = System.getProperty(KLOGGER_FACTORY, "");
		if (StringUtils.isNotEmpty(factoryClazz)) {
			try {
				return (IKLoggerFactory) Class.forName(factoryClazz).getDeclaredConstructor(new Class<?>[] {})
						.newInstance(new Object[] {});
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException
					| ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		List<IKLoggerFactory> factories = Stream
				.of(ServiceLoader.load(IKLoggerFactory.class, LoggerUtils.class.getClassLoader()).iterator()).toList();
		if (factories.size() >= 2) {
			return new MultiKLoggerFactory(factories);
		} else if (factories.size() == 1) {
			return factories.get(0);
		}
		return new Slf4jKLoggerFactory();
	}

}
