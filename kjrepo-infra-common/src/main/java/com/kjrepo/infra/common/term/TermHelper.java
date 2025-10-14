package com.kjrepo.infra.common.term;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;

import com.github.phantomthief.util.ThrowableRunnable;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class TermHelper {

	public static void addFirstTerm(String module, ThrowableRunnable<? extends Throwable> runnable) {
		addTerm(module, terms.stream().sorted((a, b) -> a.getPriority() - b.getPriority()).findFirst()
				.map(t -> t.getPriority() - 10).orElseGet(() -> defTermsPriority.getAndAdd(10)), runnable);
	}

	public static void addTerm(String module, ThrowableRunnable<? extends Throwable> runnable) {
		addTerm(module, defTermsPriority.getAndAdd(10), runnable);
	}

	public static void addTerm(String module, int priority, ThrowableRunnable<? extends Throwable> runnable) {
		terms.add(new Term(module, priority, runnable));
	}

	public static boolean isStopping() {
		return stopping.get();
	}

	private static final Logger logger = LoggerUtils.logger();
	private static final AtomicBoolean stopping = new AtomicBoolean(false);
	private static final Set<Term> terms = Sets.newConcurrentHashSet();
	private static final AtomicInteger defTermsPriority = new AtomicInteger(0);
	static {
		SignalHelper.handle("TERM", signal -> {
			try {
				stopping.set(true);
				terms.stream().sorted((a, b) -> a.getPriority() - b.getPriority()).forEach(term -> {
					Stopwatch stopwatch = Stopwatch.createStarted();
					try {
						term.getRunnable().run();
						logger.info("Signal-TERM FINISH module:{} priority:{} elapsed:{}s", term.getModule(),
								term.getPriority(), stopwatch.elapsed(TimeUnit.SECONDS));
					} catch (Throwable e) {
						logger.error("Signal-TERM ERROR module:{} priority:{} elapsed:{}s", term.getModule(),
								term.getPriority(), stopwatch.elapsed(TimeUnit.SECONDS), e);
					}
				});
			} finally {
				System.exit(signal.getNumber());
			}
		});
	}

}
