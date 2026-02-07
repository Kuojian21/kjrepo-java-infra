package com.kjrepo.infra.register.context;

import com.kjrepo.infra.common.utils.StackUtils;
import com.kjrepo.infra.loader.LoaderFactory;

public class RegisterFactory {

	private static final LoaderFactory<IRegisterContext> loaderFactory = new LoaderFactory<>(IRegisterContext.class);

	public static IRegisterContext getContext() {
		return getContext(StackUtils.firstBusinessInvokerClassname());
	}

	public static IRegisterContext getContext(Class<?> clazz) {
		return loaderFactory.getContext(clazz);
	}

	public static IRegisterContext getContext(String name) {
		return loaderFactory.getContext(name);
	}

}
