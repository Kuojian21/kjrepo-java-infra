package com.kjrepo.infra.gregister.context;

import com.kjrepo.infra.loader.LoaderFactory;

public class GroupRegisterFactory {

	private static final LoaderFactory<IGroupRegisterContext> loaderFactory = new LoaderFactory<>(
			IGroupRegisterContext.class);

	public static IGroupRegisterContext getContext(Class<?> clazz) {
		return loaderFactory.getContext(clazz);
	}

	public static IGroupRegisterContext getContext(String name) {
		return loaderFactory.getContext(name);
	}

}
