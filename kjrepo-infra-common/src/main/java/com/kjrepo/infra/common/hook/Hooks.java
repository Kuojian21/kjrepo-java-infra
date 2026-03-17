package com.kjrepo.infra.common.hook;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;

import com.annimon.stream.function.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.kjrepo.infra.common.function.ThrowableRunnable;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.common.trace.TraceIDUtils;

public class Hooks {

	public static Hooks of(String module) {
		return new Hooks(module);
	}

	private final Logger logger = LoggerUtils.logger(this.getClass());
	private final List<ThrowableRunnable<? extends Throwable>> hooks = Lists.newCopyOnWriteArrayList();

	public Hooks(String module) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				TraceIDUtils.generate();
				AtomicInteger no = new AtomicInteger(0);
				hooks.forEach(hook -> {
					no.incrementAndGet();
					Stopwatch stopwatch = Stopwatch.createStarted();
					Function<String, String> msg = result -> new StringSubstitutor(key -> {
						switch (key) {
						case "module":
							return module;
						case "no":
							return no.get() + "";
						case "result":
							return result;
						case "elapsed":
							return N_humanUtils.formatMills(stopwatch.elapsed(TimeUnit.MILLISECONDS));
						default:
							return key;
						}
					}).replace("The hook:${module}-${no} run ${result},elapsed:${elapsed}!!!");
					try {
						hook.run();
						logger.info(msg.apply("completely"));
					} catch (Throwable e) {
						logger.error(msg.apply("wrongly"), e);
					}
				});
				TraceIDUtils.clear();
			}
		}));
	}

	public void add(ThrowableRunnable<? extends Throwable> hook) {
		this.hooks.add(hook);
	}

}
