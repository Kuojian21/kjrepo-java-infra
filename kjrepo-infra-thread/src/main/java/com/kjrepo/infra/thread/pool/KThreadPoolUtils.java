package com.kjrepo.infra.thread.pool;

import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.kjrepo.infra.common.utils.ProxyUtils;
import com.kjrepo.infra.common.utils.RunUtils;

public class KThreadPoolUtils {

	private static final Method shutdownBlockingMethod = RunUtils
			.run(() -> KExecutorService.class.getDeclaredMethod("shutdownBlocking", new Class<?>[] {}));

	public static KExecutorService newExecutor(KThreadPoolInfo info) {
		if (info.getCorePoolSize() <= 0 || info.getMaximumPoolSize() <= 0) {
			info.setCorePoolSize(Math.min(6, Runtime.getRuntime().availableProcessors()));
			info.setMaximumPoolSize(Math.min(6, Runtime.getRuntime().availableProcessors()));
		}
		if (info.getKeepAliveTime() <= 0 || info.getUnit() == null) {
			info.setKeepAliveTime(10);
			info.setUnit(TimeUnit.MINUTES);
		}
		if (info.getWorkQueue() == null) {
			info.setWorkQueue(new LinkedBlockingQueue<>());
		}
		if (info.getThreadFactory() == null) {
			info.setThreadFactory(new KThreadFactory());
		}
		if (info.getRejectedHandler() == null) {
			info.setRejectedHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		}
		ThreadPoolExecutor executor = new ThreadPoolExecutor(info.getCorePoolSize(), info.getMaximumPoolSize(),
				info.getKeepAliveTime(), info.getUnit(), info.getWorkQueue(), info.getThreadFactory(),
				info.getRejectedHandler());
		return ProxyUtils.jvm(KExecutorService.class, (obj, method, args, proxy) -> {
			if (method.equals(shutdownBlockingMethod)) {
				executor.shutdown();
				while (!executor.isTerminated()) {
					executor.awaitTermination(10, TimeUnit.SECONDS);
				}
				return null;
			} else {
				return method.invoke(executor, args);
			}
		});
	}

}
