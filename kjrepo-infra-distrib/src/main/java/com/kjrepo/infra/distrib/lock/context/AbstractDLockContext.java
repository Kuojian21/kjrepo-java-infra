package com.kjrepo.infra.distrib.lock.context;

import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.kjrepo.infra.distrib.lock.DLock;
import com.kjrepo.infra.distrib.lock.nolock.NoDLock;

public abstract class AbstractDLockContext implements IDLockContext {

	private final ConcurrentMap<String, DLock> registers = Maps.newConcurrentMap();

	@Override
	public final DLock getLock(String key) {
		if (StringUtils.isEmpty(key)) {
			return new NoDLock(key);
		}
		return registers.computeIfAbsent(key, cl -> newLock(key));
	}

	public abstract DLock newLock(String key);

}
