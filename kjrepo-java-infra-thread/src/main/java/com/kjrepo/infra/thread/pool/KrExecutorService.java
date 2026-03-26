package com.kjrepo.infra.thread.pool;

import java.util.concurrent.ExecutorService;

public interface KrExecutorService extends ExecutorService, AutoCloseable {

	void shutdownBlocking();

	@Override
	void close();

}
