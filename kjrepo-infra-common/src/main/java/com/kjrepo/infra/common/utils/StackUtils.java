package com.kjrepo.infra.common.utils;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Stream;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class StackUtils {

	public static String firstBusinessInvokerClassname() {
		return firstBusinessInvokerElement().getClassName();
	}

	public static StackTraceElement firstBusinessInvokerElement() {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		StackTraceElement element = Stream.of(elements) //
				.filter(e -> StringUtils.isNotEmpty(e.getClassName()))
				.filter(e -> !"java.lang.Thread".equals(e.getClassName()))
				.filter(e -> !e.getClassName().startsWith("com.kjrepo.infra")).findFirst().orElse(elements[1]);
		if (Stream.of(elements)
				.allMatch(e -> !"<clinit>".equals(e.getMethodName()) && !"<init>".equals(e.getMethodName()))) {
			LoggerUtils.logger(StackUtils.class).error(
					"please invoke this method in <clinit> or <init> method!!! location:{}.{}:{}",
					element.getClassName(), element.getMethodName(), element.getLineNumber());
		}
		return element;
	}

}
