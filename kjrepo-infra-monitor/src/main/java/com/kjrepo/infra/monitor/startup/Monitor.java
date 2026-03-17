package com.kjrepo.infra.monitor.startup;

import java.lang.reflect.InvocationTargetException;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.google.common.collect.Sets;
import com.kjrepo.infra.common.lazy.LazyRunnable;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.trace.TraceIDUtils;
import com.kjrepo.infra.monitor.IMonitor;

public class Monitor {

	private static final Logger logger = LoggerUtils.logger(Monitor.class);
	private static final LazySupplier<Set<IMonitor>> monitors = LazySupplier
			.wrap(() -> Sets.newConcurrentHashSet(ServiceLoader.load(IMonitor.class)));
	private static final LazyRunnable startup = LazyRunnable.wrap(() -> {
		Thread thread = new Thread(() -> {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					TraceIDUtils.generate();
					monitors.get().forEach(m -> {
						m.monitor();
					});
					TraceIDUtils.clear();
					Thread.sleep(TimeUnit.MINUTES.toMillis(1));
				}
			} catch (InterruptedException e) {
				logger.error("", e);
			}
		}, "monitor");
		thread.setDaemon(true);
		thread.start();
	});

	public static void start() {
		startup.run();
	}

	public static <M extends IMonitor> void register(Class<M> clazz) {
		try {
			register(clazz.getConstructor(new Class<?>[] {}).newInstance(new Object[] {}));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			logger.error("", e);
		}
	}

	public static <M extends IMonitor> void register(IMonitor monitor) {
		monitors.get().add(monitor);
	}

}
