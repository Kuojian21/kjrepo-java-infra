package com.kjrepo.infra.text.json.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.text.json.JsonUtils;
import com.kjrepo.infra.text.json.config.ConstructorConfig;
import com.kjrepo.infra.text.json.config.MethodConfig;

public class InvokerUtils {

	private static final Logger logger = LoggerUtils.logger();

	public static Object invokeList(Object obj, List<?> list) {
		try {
			List<Object> objs = Lists.newArrayList();
			objs.add(obj);
			int j = 0;
			while (j + 2 < list.size()) {
				Class<?> clazz = Class.forName((String) list.get(j));
				String sign = (String) list.get(j + 1);
				if (sign.startsWith("(")) {
					objs.add(invoke(obj, new ConstructorConfig(Maps.newHashMap()).element(clazz, sign),
							list.get(j + 2)));
				} else {
					Method method = new MethodConfig(Maps.newHashMap()).element(clazz, sign);
					if (Modifier.isStatic(method.getModifiers())) {
						objs.add(invoke(null, method, list.get(j + 2)));
					} else {
						objs.add(invoke(objs.get((int) list.get(j + 3)), method, list.get(j + 2)));
						j++;
					}
				}
				j += 3;
			}
			return j < list.size() ? objs.get((int) list.get(j)) : objs.get(objs.size() - 1);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			logger.error("{}", JsonUtils.toPrettyJson(list), e);
			throw new RuntimeException(e);
		}
	}

	public static Object invoke(Object obj, Method method, Object param)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException,
			InstantiationException, NoSuchMethodException, SecurityException {
		return method.invoke(obj, ArgsUtils.args(obj, method.getParameterTypes(), param, Maps.newHashMap()));
	}

	public static Object invoke(Object obj, Constructor<?> constructor, Object param)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			ClassNotFoundException, NoSuchMethodException, SecurityException {
		return constructor.newInstance(ArgsUtils.args(obj, constructor.getParameterTypes(), param, Maps.newHashMap()));
	}

}
