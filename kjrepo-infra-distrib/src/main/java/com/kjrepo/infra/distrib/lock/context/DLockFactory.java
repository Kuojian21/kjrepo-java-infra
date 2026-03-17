package com.kjrepo.infra.distrib.lock.context;

import com.kjrepo.infra.common.spi.PkgSpiFactory;

public class DLockFactory {

	private static final PkgSpiFactory<IDLockContext> spi = PkgSpiFactory.of(IDLockContext.class);

	public static IDLockContext getContext(Class<?> clazz) {
		return spi.get(clazz);
	}

	public static IDLockContext getContext(String name) {
		return spi.get(name);
	}

}
