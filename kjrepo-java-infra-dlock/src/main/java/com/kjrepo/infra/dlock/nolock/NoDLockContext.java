package com.kjrepo.infra.dlock.nolock;

import com.kjrepo.infra.dlock.DLock;
import com.kjrepo.infra.dlock.context.AbstractDLockContext;
import com.kjrepo.infra.dlock.context.DLockContext;

public class NoDLockContext extends AbstractDLockContext implements DLockContext {

	@Override
	public DLock newLock(String key) {
		return new NoDLock(key);
	}

	@Override
	public String pkg() {
		return "";
	}

}
