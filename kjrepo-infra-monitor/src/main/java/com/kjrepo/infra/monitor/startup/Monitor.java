package com.kjrepo.infra.monitor.startup;

import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import com.kjrepo.infra.common.lazy.LazyRunnable;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.monitor.IMonitor;
import com.kjrepo.infra.runner.sch.ksch.KschRunner;

public class Monitor {

	private static final LazySupplier<List<IMonitor>> montiors = LazySupplier
			.wrap(() -> Lists.newArrayList(ServiceLoader.load(IMonitor.class)));
	private static final LazyRunnable startup = LazyRunnable.wrap(() -> {
		new KschRunner() {
			@Override
			public long run() throws Exception {
				montiors.get().forEach(m -> {
					m.monitor();
				});
				return TimeUnit.MINUTES.toMillis(1);
			}

			public String module() {
				return "monitor";
			}
		}.execute();
	});

	public static void start() {
		startup.run();
	}

}
