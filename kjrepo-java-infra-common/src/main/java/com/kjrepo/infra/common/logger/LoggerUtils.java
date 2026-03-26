package com.kjrepo.infra.common.logger;

import java.util.List;

import org.slf4j.Logger;

import com.kjrepo.infra.common.spi.SpiFactory;
import com.kjrepo.infra.common.utils.StackUtils;

public class LoggerUtils {

	public static Logger getLogger() {
		return logger();
	}

	public static Logger getLogger(Class<?> clazz) {
		return logger(clazz);
	}

	public static Logger getLogger(String name) {
		return logger(name);
	}

	public static Logger logger() {
		return logger(StackUtils.firstBusinessInvokerClassname());
	}

	public static Logger logger(Class<?> clazz) {
		return logger(clazz.getName());
	}

	public static Logger logger(String name) {
		return factory.getLogger(name);
	}

	public static final IKLoggerFactory factory = factory();
	public static final Logger logger = factory.getLogger(LoggerUtils.class);

	private static IKLoggerFactory factory() {
		List<IKLoggerFactory> factories = SpiFactory.list(IKLoggerFactory.class);
		if (factories.size() >= 2) {
			return new MultiKLoggerFactory(factories);
		} else if (factories.size() == 1) {
			return factories.get(0);
		}
		return new Slf4jKLoggerFactory();
	}

}
