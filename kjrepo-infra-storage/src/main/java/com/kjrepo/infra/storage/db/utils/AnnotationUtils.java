package com.kjrepo.infra.storage.db.utils;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.kjrepo.infra.common.utils.ProxyUtils;

public class AnnotationUtils {

	public static <T extends Annotation> T create(Class<T> annotationClass, Map<String, Object> data) {

		return ProxyUtils.proxy(annotationClass, (obj, method, args, proxy) -> {
			return data.getOrDefault(method.getName(), method.getDefaultValue());
		});

	}

}
