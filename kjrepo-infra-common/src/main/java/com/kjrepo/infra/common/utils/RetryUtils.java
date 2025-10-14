package com.kjrepo.infra.common.utils;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.annimon.stream.function.Function;
import com.annimon.stream.function.ThrowableSupplier;
import com.github.phantomthief.util.ThrowableRunnable;
import com.google.common.util.concurrent.Uninterruptibles;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class RetryUtils {

	private static final ThreadLocal<String> msg = new ThreadLocal<>();

	private static final Logger logger = LoggerUtils.logger();

	public static <X extends Throwable> void run(ThrowableRunnable<X> runnable, int times, long sleep) {
		run(runnable, times, sleep, e -> true);
	}

	public static <X extends Throwable> void run(ThrowableRunnable<X> runnable, int times, long sleep,
			Function<Throwable, Boolean> retryable) {
		try {
			call(() -> {
				runnable.run();
				return null;
			}, times, sleep, retryable);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <T, X extends Throwable> T run(ThrowableSupplier<T, X> supplier, int times, long sleep) {
		return run(supplier, times, sleep, t -> true);
	}

	public static <T, X extends Throwable> T run(ThrowableSupplier<T, X> supplier, int times, long sleep,
			Function<Throwable, Boolean> retryable) {
		try {
			return call(() -> supplier.get(), times, sleep, retryable);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <T, X extends Throwable> T call(ThrowableSupplier<T, X> supplier, int times, long sleep) throws X {
		return call(supplier, times, sleep, t -> true);
	}

	public static <T, X extends Throwable> T call(ThrowableSupplier<T, X> supplier, int times, long sleep,
			Function<Throwable, Boolean> retryable) throws X {
		msg.set("");
		for (int i = 0; i < times - 1; i++) {
			try {
				return supplier.get();
			} catch (Throwable e) {
				logger.info("Retry:" + msg.get(), e);
				if (retryable.apply(e)) {
					Uninterruptibles.sleepUninterruptibly(sleep, TimeUnit.MILLISECONDS);
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

	public static void msg(String smsg) {
		msg.set(smsg);
	}

}
