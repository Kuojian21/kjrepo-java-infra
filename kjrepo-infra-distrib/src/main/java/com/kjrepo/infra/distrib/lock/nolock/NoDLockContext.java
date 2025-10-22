package com.kjrepo.infra.distrib.lock.nolock;

import com.kjrepo.infra.distrib.lock.DLock;
import com.kjrepo.infra.distrib.lock.context.AbstractDLockContext;
import com.kjrepo.infra.distrib.lock.context.IDLockContext;

public class NoDLockContext extends AbstractDLockContext implements IDLockContext {

	@Override
	public DLock newLock(String key) {
		return new NoDLock(key);
	}

	@Override
	public String pkg() {
		return "com";
	}

}
