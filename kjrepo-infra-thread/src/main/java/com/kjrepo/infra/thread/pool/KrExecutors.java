package com.kjrepo.infra.thread.pool;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.kjrepo.infra.common.utils.ProxyUtils;
import com.kjrepo.infra.common.utils.RunUtils;
import com.kjrepo.infra.trace.utils.TraceIDUtils;

public class KrExecutors {

	private static final Method shutdownBlockingMethod = RunUtils
			.run(() -> KrExecutorService.class.getDeclaredMethod("shutdownBlocking", new Class<?>[] {}));

	public static KrExecutorService newExecutor(KrExecutorServiceInfo info) {

		info = Optional.ofNullable(info).orElseGet(KrExecutorServiceInfo::new);
		info.ensure();

		ThreadPoolExecutor executor = new ThreadPoolExecutor( //
				info.getCorePoolSize(), //
				info.getMaximumPoolSize(), //
				info.getKeepAliveTime(), //
				info.getUnit(), //
				info.getWorkQueue(), //
				info.getThreadFactory(), //
				info.getRejectedHandler() //
		);
		return ProxyUtils.jvm(KrExecutorService.class, (obj, method, args, proxy) -> {
			if (method.equals(shutdownBlockingMethod)) {
				executor.shutdown();
				while (!executor.isTerminated()) {
					executor.awaitTermination(10, TimeUnit.SECONDS);
				}
				return null;
			} else if (method.getDeclaringClass() == Executor.class
					|| method.getDeclaringClass() == ExecutorService.class) {
				switch (method.getName()) {
				case "execute":
				case "submit":
				case "invokeAll":
				case "invokeAny":
					return method.invoke(executor, Stream.of(args).map(KrExecutors::wrap).toArray());
				default:
					return method.invoke(executor, args);
				}
			} else {
				return method.invoke(executor, args);
			}
		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static Object wrap(Object obj) {
		if (obj instanceof Runnable) {
			String traceid = TraceIDUtils.get();
			return new Runnable() {
				@Override
				public void run() {
					TraceIDUtils.set(traceid);
					try {
						((Runnable) obj).run();
					} finally {
						TraceIDUtils.clear();
					}
				}
			};
		} else if (obj instanceof Callable) {
			String traceid = TraceIDUtils.get();
			return new Callable() {
				@Override
				public Object call() throws Exception {
					TraceIDUtils.set(traceid);
					try {
						return ((Callable) obj).call();
					} finally {
						TraceIDUtils.clear();
					}
				}
			};
		} else if (obj instanceof Collection) {
			return (Collection) Stream.of((Collection) obj).map(KrExecutors::wrap).toList();
		} else {
			return obj;
		}
	}

}
