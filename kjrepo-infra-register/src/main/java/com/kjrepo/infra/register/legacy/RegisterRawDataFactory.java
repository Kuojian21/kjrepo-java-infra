package com.kjrepo.infra.register.legacy;

import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;

import com.annimon.stream.Stream;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class RegisterRawDataFactory {

	private static final Logger logger = LoggerUtils.logger(IRegisterRawDataHandler.class);
	private static final List<IRegisterRawDataHandler> handlers;
	static {
		try {
			handlers = Stream.of(ServiceLoader.load(IRegisterRawDataHandler.class)).toList();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void handle(Class<?> clazz, Object obj) {
		try {
			for (IRegisterRawDataHandler handler : handlers) {
				if (handler.forClazz().isAssignableFrom(clazz)) {
					handler.handle(obj);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
