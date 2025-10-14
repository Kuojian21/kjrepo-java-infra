package com.kjrepo.infra.storage.utils;

import java.io.Closeable;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.pool2.ObjectPool;

import com.kjrepo.infra.storage.RepositoryRuntimeException;

public class ResourceUtils {

	public static <R extends Closeable> void execute(R resource, Consumer<R> consumer) {
		execute(resource, r -> {
			consumer.accept(r);
			return null;
		});
	}

	public static <T, R extends AutoCloseable> T execute(R resource, Function<R, T> func) {
		try (AutoCloseable closeable = resource) {
			return func.apply(resource);
		} catch (Exception e) {
			throw new RepositoryRuntimeException(e);
		}
	}

	public static <R> void poolExecute(ObjectPool<R> pool, Consumer<R> consumer) {
		poolExecute(pool, r -> {
			consumer.accept(r);
			return null;
		});
	}

	public static <T, R> T poolExecute(ObjectPool<R> pool, Function<R, T> func) {
		try {
			R resource = pool.borrowObject();
			try {
				return func.apply(resource);
			} finally {
				pool.returnObject(resource);
			}
		} catch (Exception e) {
			throw new RepositoryRuntimeException(e);
		}
	}

}
