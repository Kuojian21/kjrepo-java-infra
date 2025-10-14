package com.kjrepo.infra.distrib.lock;

import java.util.concurrent.TimeUnit;

public interface DLock {

	void lock();

	boolean tryLock();

	boolean tryLock(long time, TimeUnit unit);

	void unlock();
}
