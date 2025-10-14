package com.kjrepo.infra.distrib.lock.context;

import com.kjrepo.infra.loader.LoaderFactory;

public class DLockFactory {

	private static final LoaderFactory<DLockContext> loaderFactory = new LoaderFactory<>(DLockContext.class);

	public static DLockContext getContext(Class<?> clazz) {
		return loaderFactory.getContext(clazz);
	}

	public static DLockContext getContext(String name) {
		return loaderFactory.getContext(name);
	}

}
