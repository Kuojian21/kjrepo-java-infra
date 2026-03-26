package com.kjrepo.infra.network.capture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openqa.selenium.chrome.ChromeDriver;

import com.google.common.util.concurrent.SettableFuture;

public abstract class ChromeCaptureHandler<T> implements Future<T> {

	private final SettableFuture<T> future = SettableFuture.create();

	public abstract void doHandle(ChromeDriver driver, Object arg);

	public final void handle(ChromeDriver driver, Object arg) {
		if (this.future.isCancelled()) {
			return;
		}
		doHandle(driver, arg);
	}

	public final void set(T data) {
		this.future.set(data);
	}

	@Override
	public final T get() throws InterruptedException, ExecutionException {
		return this.future.get();
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return this.future.get(timeout, unit);
	}

	@Override
	public final boolean isDone() {
		return this.future.isDone();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return future.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return future.isCancelled();
	}

}
