package com.kjrepo.infra.dlock.nolock;

import java.util.concurrent.TimeUnit;

import com.kjrepo.infra.dlock.AbstractDLock;

public class NoDLock extends AbstractDLock {

	public NoDLock(String key) {
		super(key);
	}

	@Override
	public boolean tryLock(long timeout, TimeUnit unit) {
		logger.debug("nolock-key:{}", this.key());
		return true;
	}

	@Override
	public void unlock() {

	}

}
