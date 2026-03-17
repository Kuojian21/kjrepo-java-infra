package com.kjrepo.infra.distrib.lock.context;

import com.kjrepo.infra.common.spi.PkgSpi;
import com.kjrepo.infra.distrib.lock.DLock;

public interface IDLockContext extends PkgSpi {

	DLock getLock(String key);

	default String pkg() {
		return "";
	}

}
