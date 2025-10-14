package com.kjrepo.infra.distrib.lock.context;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.kjrepo.infra.distrib.lock.DLock;

public abstract class AbstractDLockContext implements DLockContext {

	private final ConcurrentMap<String, DLock> registers = Maps.newConcurrentMap();

	@Override
	public DLock getLock(String key) {
		return registers.computeIfAbsent(key, cl -> newLock(key));
	}

	public abstract DLock newLock(String key);

}
