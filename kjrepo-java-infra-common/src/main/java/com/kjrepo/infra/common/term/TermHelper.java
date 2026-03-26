package com.kjrepo.infra.common.term;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;

import com.annimon.stream.function.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.kjrepo.infra.common.function.ThrowableRunnable;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.common.trace.TraceIDUtils;

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

	private static final Logger logger = LoggerUtils.logger(TermHelper.class);
	private static final AtomicBoolean stopping = new AtomicBoolean(false);
	private static final Set<Term> terms = Sets.newConcurrentHashSet();
	private static final AtomicInteger defTermsPriority = new AtomicInteger(0);
	static {
		SignalHelper.handle("TERM", signal -> {
			try {
				TraceIDUtils.generate();
				stopping.set(true);
				Stopwatch st = Stopwatch.createStarted();
				logger.info("The terms-execution will be run!!!");
				terms.stream().sorted((a, b) -> Integer.compare(a.getPriority(), b.getPriority())).forEach(term -> {
					Stopwatch stopwatch = Stopwatch.createStarted();
					Function<String, String> msg = result -> new StringSubstitutor(key -> {
						switch (key) {
						case "module":
							return term.getModule();
						case "priority":
							return term.getPriority() + "";
						case "result":
							return result;
						case "elapsed":
							return N_humanUtils.formatMills(stopwatch.elapsed(TimeUnit.MILLISECONDS));
						default:
							return key;
						}
					}).replace("The term module:${module} priority:${priority} run ${result},elapsed:${elapsed}!!!");
					try {
						term.getRunnable().run();
						logger.info(msg.apply("completely"));
					} catch (Throwable e) {
						logger.error(msg.apply("wrongly"), e);
					}
				});
				logger.info("The terms-execution has been finished,elapsed:{}!!!",
						N_humanUtils.formatMills(st.elapsed(TimeUnit.MILLISECONDS)));
				TraceIDUtils.clear();
			} finally {
				System.exit(signal.getNumber());
			}
		});
	}

}
