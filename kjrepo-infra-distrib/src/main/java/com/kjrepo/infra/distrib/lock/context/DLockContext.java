package com.kjrepo.infra.distrib.lock.context;

import com.kjrepo.infra.distrib.lock.DLock;
import com.kjrepo.infra.loader.Loader;

public interface DLockContext extends Loader {

	DLock getLock(String key);

	default String pkg() {
		return "";
	}

}
