package com.kjrepo.infra.thread.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.annimon.stream.function.ThrowableFunction;

public class MapperFuture<V, T> implements Future<T> {

	public static <V, T> Future<T> wrap(Future<V> future, ThrowableFunction<V, T, Exception> function) {
		return new MapperFuture<>(future, function);
	}

	private final Future<V> future;
	private final ThrowableFunction<V, T, Exception> function;

	public MapperFuture(Future<V> future, ThrowableFunction<V, T, Exception> function) {
		super();
		this.future = future;
		this.function = function;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return future.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return future.isCancelled();
	}

	@Override
	public boolean isDone() {
		return future.isDone();
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		try {
			return this.function.apply(future.get());
		} catch (InterruptedException | ExecutionException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		try {
			return this.function.apply(future.get(timeout, unit));
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
