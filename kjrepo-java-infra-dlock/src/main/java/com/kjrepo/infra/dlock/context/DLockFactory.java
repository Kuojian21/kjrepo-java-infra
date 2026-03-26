package com.kjrepo.infra.dlock.context;

import com.kjrepo.infra.common.spi.PkgSpiFactory;
import com.kjrepo.infra.common.utils.StackUtils;

public class DLockFactory {

	private static final PkgSpiFactory<DLockContext> spi = PkgSpiFactory.of(DLockContext.class);

	public static DLockContext getContext() {
		return getContext(StackUtils.firstBusinessInvokerClassname());
	}

	public static DLockContext getContext(Class<?> clazz) {
		return spi.get(clazz);
	}

	public static DLockContext getContext(String name) {
		return spi.get(name);
	}

}
