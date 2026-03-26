package com.kjrepo.infra.dlock.context;

import com.kjrepo.infra.common.spi.PkgSpi;
import com.kjrepo.infra.dlock.DLock;

public interface DLockContext extends PkgSpi {

	DLock getLock(String key);

	default String pkg() {
		return "";
	}

}
