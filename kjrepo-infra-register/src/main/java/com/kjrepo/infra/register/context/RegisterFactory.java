package com.kjrepo.infra.register.context;

import com.kjrepo.infra.loader.LoaderFactory;

public class RegisterFactory {

	private static final LoaderFactory<IRegisterContext> loaderFactory = new LoaderFactory<>(IRegisterContext.class);

	public static IRegisterContext getContext(Class<?> clazz) {
		return loaderFactory.getContext(clazz);
	}

	public static IRegisterContext getContext(String name) {
		return loaderFactory.getContext(name);
	}

}
