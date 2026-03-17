package com.kjrepo.infra.register.group.context;

import com.kjrepo.infra.common.spi.PkgSpiFactory;
import com.kjrepo.infra.common.utils.StackUtils;

public class GroupRegisterFactory {

	private static final PkgSpiFactory<IGroupRegisterContext> spi = PkgSpiFactory.of(IGroupRegisterContext.class);

	public static IGroupRegisterContext getContext() {
		return getContext(StackUtils.firstBusinessInvokerClassname());
	}

	public static IGroupRegisterContext getContext(Class<?> clazz) {
		return spi.get(clazz);
	}

	public static IGroupRegisterContext getContext(String name) {
		return spi.get(name);
	}

}
