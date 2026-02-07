package com.kjrepo.infra.thread.utils;

import java.util.Collection;
import java.util.concurrent.Callable;

import com.annimon.stream.Stream;
import com.kjrepo.infra.common.trace.TraceIDUtils;

public class KrThread {

	public static Thread of(Runnable runnable) {
		return new Thread(wrap(runnable));
	}

	public static Object wrap(Object obj) {
		if (obj instanceof Runnable) {
			return wrap((Runnable) obj);
		} else if (obj instanceof Callable) {
			return wrap((Callable<?>) obj);
		} else if (obj instanceof Collection) {
			return Stream.of((Collection<?>) obj).map(KrThread::wrap).toList();
		} else {
			return obj;
		}
	}

	public static Runnable wrap(Runnable obj) {
		return new Runnable() {
			@Override
			public void run() {
				TraceIDUtils.generate(TraceIDUtils.get());
				try {
					((Runnable) obj).run();
				} finally {
					TraceIDUtils.clear();
				}
			}
		};
	}

	public static Callable<?> wrap(Callable<?> callable) {
		return new Callable<>() {
			@Override
			public Object call() throws Exception {
				TraceIDUtils.generate(TraceIDUtils.get());
				try {
					return callable.call();
				} finally {
					TraceIDUtils.clear();
				}
			}
		};
	}

}
