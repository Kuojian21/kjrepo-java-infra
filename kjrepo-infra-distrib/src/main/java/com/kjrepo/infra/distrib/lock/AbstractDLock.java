package com.kjrepo.infra.distrib.lock;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;

public abstract class AbstractDLock implements DLock {

	protected final Logger logger = LoggerUtils.logger(getClass());

	private final String key;

	public AbstractDLock(String key) {
		this.key = key;
	}

	@Override
	public void lock() {
		if (!tryLock(-1, null)) {
			throw new RuntimeException("failure while trying to acquire lock: " + key);
		}
	}

	@Override
	public boolean tryLock() {
		return tryLock(1, TimeUnit.MILLISECONDS);
	}

	public String key() {
		return this.key;
	}

}
