package com.kjrepo.infra.thread.pool;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;

public interface KExecutorService extends ExecutorService, Closeable {

	void shutdownBlocking();

	@Override
	default void close() {
		this.shutdown();
	}

}
