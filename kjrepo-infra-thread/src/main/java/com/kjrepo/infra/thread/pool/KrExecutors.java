package com.kjrepo.infra.thread.pool;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.kjrepo.infra.common.utils.ProxyUtils;
import com.kjrepo.infra.thread.utils.ThreadHelper;

public class KrExecutors {

	public static KrExecutorService newExecutor(KrExecutorServiceInfo info) {
		info = Optional.ofNullable(info).orElseGet(KrExecutorServiceInfo::new);
		info.ensure();

		ThreadPoolExecutor executor = new ThreadPoolExecutor( //
				info.getCorePoolSize(), //
				info.getMaximumPoolSize(), //
				info.getKeepAliveTime(), //
				info.getUnit(), //
				info.getWorkQueue(), //
				info.getThreadFactory().build(), //
				info.getRejectedHandler() //
		);
		return wrap(executor);
	}

	public static KrExecutorService newFixedThreadPool(int nThreads) {
		return wrap(Executors.newFixedThreadPool(nThreads));
	}

	public static KrExecutorService newWorkStealingPool(int parallelism) {
		return wrap(Executors.newWorkStealingPool(parallelism));
	}

	public static KrExecutorService newWorkStealingPool() {
		return wrap(Executors.newWorkStealingPool());
	}

	public static KrExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
		return wrap(Executors.newFixedThreadPool(nThreads, threadFactory));
	}

	public static KrExecutorService newSingleThreadExecutor() {
		return wrap(Executors.newSingleThreadExecutor());
	}

	public static KrExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
		return wrap(Executors.newSingleThreadExecutor(threadFactory));
	}

	public static KrExecutorService newCachedThreadPool() {
		return wrap(Executors.newCachedThreadPool());
	}

	public static KrExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
		return wrap(Executors.newCachedThreadPool(threadFactory));
	}

	public static KrScheduledExecutorService newSingleThreadScheduledExecutor() {
		return wrap(Executors.newSingleThreadScheduledExecutor());
	}

	public static KrScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory threadFactory) {
		return wrap(Executors.newSingleThreadScheduledExecutor(threadFactory));
	}

	public static KrScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
		return wrap(Executors.newScheduledThreadPool(corePoolSize));
	}

	public static KrScheduledExecutorService newScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory) {
		return wrap(Executors.newScheduledThreadPool(corePoolSize, threadFactory));
	}

	public static KrExecutorService unconfigurableExecutorService(ExecutorService executor) {
		return wrap(Executors.unconfigurableExecutorService(executor));
	}

	public static KrScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService executor) {
		return wrap(Executors.unconfigurableScheduledExecutorService(executor));
	}

	private static KrExecutorService wrap(ExecutorService executor) {
		return wrap(executor, KrExecutorService.class);
	}

	private static KrScheduledExecutorService wrap(ScheduledExecutorService executor) {
		return wrap(executor, KrScheduledExecutorService.class);
	}

	private static <T extends KrExecutorService> T wrap(ExecutorService executor, Class<T> clazz) {
		return ProxyUtils.jvm(clazz, (obj, method, args, proxy) -> {
			if (method.getDeclaringClass() == KrExecutorService.class) {
				switch (method.getName()) {
				case "close":
					executor.shutdown();
					break;
				case "shutdownBlocking":
					executor.shutdown();
					while (!executor.isTerminated()) {
						executor.awaitTermination(10, TimeUnit.SECONDS);
					}
					break;
				default:
					throw new RuntimeException("unknow method:" + method.getName() + "!!!");
				}
				return null;
			} else if (method.getDeclaringClass() == Executor.class
					|| method.getDeclaringClass() == ExecutorService.class
					|| method.getDeclaringClass() == ScheduledExecutorService.class) {
				switch (method.getName()) {
				case "execute":
				case "submit":
				case "invokeAll":
				case "invokeAny":
				case "schedule":
				case "scheduleAtFixedRate":
				case "scheduleWithFixedDelay":
					return method.invoke(executor, Stream.of(args).map(ThreadHelper::wrap).toArray());
				default:
					return method.invoke(executor, args);
				}
			} else {
				return method.invoke(executor, args);
			}
		});
	}

}
