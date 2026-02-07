package com.kjrepo.infra.common.term;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Function;
import com.github.phantomthief.util.ThrowableRunnable;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.common.trace.TraceIDUtils;

public class HookHelper {

	private static final Logger logger = LoggerUtils.logger(HookHelper.class);
	private static final Map<String, LazySupplier<List<ThrowableRunnable<? extends Throwable>>>> hooks = Maps
			.newConcurrentMap();

	public static void addHook(String module, ThrowableRunnable<? extends Throwable> hook) {
		hooks.computeIfAbsent(Optional.ofNullable(module).orElse("def"), k -> LazySupplier.wrap(() -> {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					TraceIDUtils.generate();
					AtomicInteger no = new AtomicInteger(0);
					hooks.get(k).get().forEach(iHook -> {
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
							iHook.run();
							logger.info(msg.apply("completely"));
						} catch (Throwable e) {
							logger.error(msg.apply("wrongly"), e);
						}
					});
					TraceIDUtils.clear();
				}
			}));
			return Lists.newCopyOnWriteArrayList();
		})).get().add(hook);
	}

}
