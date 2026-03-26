package com.kjrepo.infra.dlock;

import java.util.concurrent.TimeUnit;

public interface DLock {

	void lock();

	boolean tryLock();

	boolean tryLock(long timeout, TimeUnit unit);

	void unlock();
}
