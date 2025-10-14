package com.kjrepo.infra.distrib.lock.impl;

import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.kjrepo.infra.distrib.lock.AbstractDLock;

public class CuratorDLock extends AbstractDLock {

	private final InterProcessMutex lock;

	public CuratorDLock(String key, CuratorFramework curator) {
		super(key);
		this.lock = new InterProcessMutex(curator, key);
	}

	@Override
	public boolean tryLock(long timeout, TimeUnit unit) {
		try {
			return lock.acquire(timeout, unit);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void unlock() {
		try {
			if (lock != null && lock.isAcquiredInThisProcess()) {
				lock.release();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
