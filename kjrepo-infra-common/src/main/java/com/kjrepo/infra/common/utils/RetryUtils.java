package com.kjrepo.infra.common.utils;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.ThrowableSupplier;
import com.google.common.util.concurrent.Uninterruptibles;
import com.kjrepo.infra.common.function.ThrowableRunnable;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class RetryUtils {

	private static final Logger logger = LoggerUtils.logger(RetryUtils.class);

	public static <X extends Throwable> void retry_catching(ThrowableRunnable<X> runnable, int times,
			long sleepMillis) {
		retry_catching(runnable, times, sleepMillis, null);
	}

	public static <X extends Throwable> void retry_catching(ThrowableRunnable<X> runnable, int times, long sleepMillis,
			Function<Throwable, Boolean> retryable) {
		retry_catching(() -> {
			runnable.run();
			return null;
		}, times, sleepMillis, retryable);
	}

	public static <T, X extends Throwable> T retry_catching(ThrowableSupplier<T, X> supplier, int times,
			long sleepMillis) {
		return retry_catching(supplier, times, sleepMillis, null);
	}

	public static <T, X extends Throwable> T retry_catching(ThrowableSupplier<T, X> supplier, int times,
			long sleepMillis, Function<Throwable, Boolean> retryable) {
		return RunUtils.catching(() -> {
			return retry(() -> supplier.get(), times, sleepMillis, retryable);
		});
	}

	public static <X extends Throwable> void retry_throwing(ThrowableRunnable<X> runnable, int times,
			long sleepMillis) {
		retry_throwing(runnable, times, sleepMillis, e -> true);
	}

	public static <X extends Throwable> void retry_throwing(ThrowableRunnable<X> runnable, int times, long sleepMillis,
			Function<Throwable, Boolean> retryable) {
		retry_throwing(() -> {
			runnable.run();
			return null;
		}, times, sleepMillis, retryable);
	}

	public static <T, X extends Throwable> T retry_throwing(ThrowableSupplier<T, X> supplier, int times,
			long sleepMillis) {
		return retry_throwing(supplier, times, sleepMillis, null);
	}

	public static <T, X extends Throwable> T retry_throwing(ThrowableSupplier<T, X> supplier, int times,
			long sleepMillis, Function<Throwable, Boolean> retryable) {
		return RunUtils.throwing(() -> {
			return retry(() -> supplier.get(), times, sleepMillis, retryable);
		});
	}

	public static <X extends Throwable> void retry(ThrowableRunnable<X> runnable, int times, long sleepMillis)
			throws X {
		retry(() -> {
			runnable.run();
			return null;
		}, times, sleepMillis, null);

	}

	public static <X extends Throwable> void retry(ThrowableRunnable<X> runnable, int times, long sleepMillis,
			Function<Throwable, Boolean> retryable) throws X {
		retry(() -> {
			runnable.run();
			return null;
		}, times, sleepMillis, retryable);

	}

	public static <T, X extends Throwable> T retry(ThrowableSupplier<T, X> supplier, int times, long sleepMillis)
			throws X {
		return retry(supplier, times, sleepMillis, null);
	}

	public static <T, X extends Throwable> T retry(ThrowableSupplier<T, X> supplier, int times, long sleepMillis,
			Function<Throwable, Boolean> retryable) throws X {
		msg.set("");
		for (int i = 0; i < times - 1; i++) {
			try {
				return supplier.get();
			} catch (Throwable e) {
				logger.info("Retry:" + msg.get(), e);
				if (Optional.ofNullable(retryable).orElse(t -> true).apply(e)) {
					Uninterruptibles.sleepUninterruptibly(sleepMillis, TimeUnit.MILLISECONDS);
				} else {
					throw e;
				}
			}
		}
		try {
			return supplier.get();
		} finally {
			msg.set("");
		}
	}

	private static final ThreadLocal<String> msg = new ThreadLocal<>();

	public static void msg(String smsg) {
		msg.set(smsg);
	}

}
