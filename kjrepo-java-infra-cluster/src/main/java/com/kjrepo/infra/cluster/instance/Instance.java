package com.kjrepo.infra.cluster.instance;

import org.slf4j.Logger;

import com.annimon.stream.function.Supplier;
import com.annimon.stream.function.ThrowableConsumer;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class Instance<R> implements Supplier<R>, AutoCloseable {

	public static <R> Instance<R> of(String name, R resource, ThrowableConsumer<R, Exception> release) {
		return new Instance<R>(name, resource, release);
	}

	private final Logger logger = LoggerUtils.logger(getClass());

	private final String name;
	private final R resource;
	private final ThrowableConsumer<R, Exception> release;

	public Instance(String name, R resource, ThrowableConsumer<R, Exception> release) {
		this.name = name;
		this.resource = resource;
		this.release = release;
	}

	@Override
	public R get() {
		return this.resource;
	}

	public String name() {
		return this.name;
	}

	@Override
	public void close() {
		try {
			if (this.release != null) {
				this.release.accept(resource);
			} else if (resource instanceof AutoCloseable) {
				((AutoCloseable) resource).close();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
