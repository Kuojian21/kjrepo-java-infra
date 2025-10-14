package com.kjrepo.infra.text.json.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;

import com.annimon.stream.Stream;
import com.kjrepo.infra.common.logger.LoggerUtils;

class ParameterizedTypeTest extends IParameterizedTypeTest<String> {

	public static final Logger logger = LoggerUtils.logger();

	public void test1(String bean) {
		logger.info("{}", bean);
	}

	public void test2(String bean) {
		logger.info("{}", bean);
	}

	public static void main(String[] args) {
		Stream.of(ParameterizedTypeTest.class.getDeclaredMethods()).forEach(method -> {
			if (method.getName().startsWith("test")) {
				logger.info("class:{}, name:{} parameter:{}", method.getDeclaringClass(), method.getName(),
						method.getGenericParameterTypes()[0]);
			}
		});
		Stream.of(IParameterizedTypeTest.class.getDeclaredMethods()).forEach(method -> {
			logger.info("class:{}, name:{} parameter:{}", method.getDeclaringClass(), method.getName(),
					method.getGenericParameterTypes()[0]);
		});
		logger.info("{}", ParameterizedTypeTest.class.getGenericSuperclass());
		Type[] types1 = ((ParameterizedType) ParameterizedTypeTest.class.getGenericSuperclass())
				.getActualTypeArguments();
		Type[] types2 = ((Class<?>) ((ParameterizedType) ParameterizedTypeTest.class.getGenericSuperclass())
				.getRawType()).getTypeParameters();
		for (int i = 0; i < types1.length; i++) {
			logger.info("{} {}", types1[i].toString(), types2[i].toString());
		}

		ParameterizedType pType = (ParameterizedType) ParameterizedTypeTest.class.getGenericSuperclass();
		Class<?> pClazz = (Class<?>) pType.getRawType();
		Stream.of(IParameterizedTypeTest.class.getDeclaredMethods()).forEach(method -> {
			if (method.getName().startsWith("test")) {
				logger.info("{} {}", method.getName(),
						pClazz.getTypeParameters()[0] == method.getGenericParameterTypes()[0]);
			}
		});
	}

}
