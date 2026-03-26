package com.kjrepo.infra.common.scanner;

import com.annimon.stream.function.Predicate;

public class ClazzScanner extends Scanner<Class<?>> {

	public static ClazzScanner of(String pkg, Predicate<Class<?>> filter) {
		return of(pkg, filter, ClazzScanner.class.getClassLoader());
	}

	public static ClazzScanner of(String pkg, Predicate<Class<?>> filter, ClassLoader clazzLoader) {
		return new ClazzScanner(pkg, filter, clazzLoader);
	}

	private ClazzScanner(String pkg, Predicate<Class<?>> filter, ClassLoader clazzLoader) {
		super(pkg.replace(".", "/"), "(" + pkg.replace(".", "/") + ".+)\\.class$",
				cls -> Class.forName(cls.replace("/", ".")), filter, clazzLoader);
	}

}
