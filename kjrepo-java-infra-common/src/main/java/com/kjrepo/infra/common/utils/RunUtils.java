package com.kjrepo.infra.common.utils;

import org.slf4j.Logger;

import com.annimon.stream.function.ThrowableSupplier;
import com.kjrepo.infra.common.function.ThrowableRunnable;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class RunUtils {

	private static final Logger logger = LoggerUtils.logger(RunUtils.class);

	public static <T, X extends Throwable> void throwing(ThrowableRunnable<X> runnable) {
		throwing(() -> {
			runnable.run();
			return null;
		});
	}

	public static <T, X extends Throwable> void catching(ThrowableRunnable<X> runnable) {
		catching(() -> {
			runnable.run();
			return null;
		});
	}

	public static <T, X extends Throwable> T throwing(ThrowableSupplier<T, X> supplier) {
		try {
			return supplier.get();
		} catch (Throwable e) {
			logger.error("", e);
			throw new RuntimeException(e);
		}
	}

	public static <T, X extends Throwable> T catching(ThrowableSupplier<T, X> supplier) {
		try {
			return supplier.get();
		} catch (Throwable e) {
			logger.error("", e);
			return null;
		}
	}

}
