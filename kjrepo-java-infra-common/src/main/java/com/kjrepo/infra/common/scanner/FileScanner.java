package com.kjrepo.infra.common.scanner;

import com.annimon.stream.function.Predicate;

public class FileScanner extends Scanner<String> {

	public static FileScanner of(String path, Predicate<String> filter) {
		return of(path, filter, ClazzScanner.class.getClassLoader());
	}

	public static FileScanner of(String path, Predicate<String> filter, ClassLoader clazzLoader) {
		return new FileScanner(path, filter, clazzLoader);
	}

	private FileScanner(String path, Predicate<String> filter, ClassLoader clazzLoader) {
		super(path, ".*" + path + ".*", s -> s, filter, clazzLoader);
	}
}
