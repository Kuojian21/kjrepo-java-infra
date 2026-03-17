package com.kjrepo.infra.register.context;

import com.kjrepo.infra.common.spi.PkgSpiFactory;
import com.kjrepo.infra.common.utils.StackUtils;

public class RegisterFactory {

	private static final PkgSpiFactory<IRegisterContext> spi = PkgSpiFactory.of(IRegisterContext.class);

	public static IRegisterContext getContext() {
		return getContext(StackUtils.firstBusinessInvokerClassname());
	}

	public static IRegisterContext getContext(Class<?> clazz) {
		return spi.get(clazz);
	}

	public static IRegisterContext getContext(String name) {
		return spi.get(name);
	}

}
