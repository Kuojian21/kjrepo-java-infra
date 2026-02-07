package com.kjrepo.infra.monitor.startup;

import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.kjrepo.infra.common.lazy.LazyRunnable;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.trace.TraceIDUtils;
import com.kjrepo.infra.monitor.IMonitor;

public class Monitor {

	private static final Logger logger = LoggerUtils.logger(Monitor.class);
	private static final LazySupplier<List<IMonitor>> montiors = LazySupplier
			.wrap(() -> Lists.newArrayList(ServiceLoader.load(IMonitor.class)));
	private static final LazyRunnable startup = LazyRunnable.wrap(() -> {
		Thread thread = new Thread(() -> {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					TraceIDUtils.generate();
					montiors.get().forEach(m -> {
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

}
