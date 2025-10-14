package com.kjrepo.infra.common.utils;

import java.util.concurrent.Callable;

public class RunUtils {

	public static <T> T run(Callable<T> callable) {
		try {
			return callable.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
