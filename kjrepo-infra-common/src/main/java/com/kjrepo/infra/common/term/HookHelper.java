package com.kjrepo.infra.common.term;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.github.phantomthief.util.ThrowableRunnable;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class HookHelper {

	private static final Logger logger = LoggerUtils.logger(HookHelper.class);
	private static final Map<String, LazySupplier<List<Hook>>> hooks = Maps.newConcurrentMap();

	public static void addHook(String module, ThrowableRunnable<? extends Throwable> hook) {
		StackTraceElement element = Stream.of(Thread.currentThread().getStackTrace())
				.filter(e -> !e.getClassName().equals("java.lang.Thread")
						&& !HookHelper.class.getName().equals(e.getClassName()))
				.findFirst().get();
		hooks.computeIfAbsent(Optional.ofNullable(module).orElse("def"), k -> LazySupplier.wrap(() -> {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					Stopwatch stopwatch = Stopwatch.createStarted();
					hooks.get(k).get().forEach(h -> {
						try {
							h.hook().run();
							logger.info("Hook FINISH module:{},{} {}:{} elapsed:{}s", module, h.no(),
									h.element().getFileName(), h.element().getLineNumber(),
									stopwatch.elapsed(TimeUnit.SECONDS));
						} catch (Throwable e) {
							logger.error("Hook ERROR module:{},{} {}:{} elapsed:{}s", module, h.no(),
									h.element().getFileName(), h.element().getLineNumber(),
									stopwatch.elapsed(TimeUnit.SECONDS), e);
						}
					});
				}
			}));
			return Lists.newCopyOnWriteArrayList();
		})).get().add(Hook.of(hook, element));

	}

}
