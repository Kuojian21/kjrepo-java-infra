package com.kjrepo.infra.distrib.lock.context;

import com.kjrepo.infra.loader.LoaderFactory;

public class DLockFactory {

	private static final LoaderFactory<IDLockContext> loaderFactory = new LoaderFactory<>(IDLockContext.class);

	public static IDLockContext getContext(Class<?> clazz) {
		return loaderFactory.getContext(clazz);
	}

	public static IDLockContext getContext(String name) {
		return loaderFactory.getContext(name);
	}

}
