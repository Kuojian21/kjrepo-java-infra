package com.kjrepo.infra.executor;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.annimon.stream.Optional;
import com.annimon.stream.function.ThrowableConsumer;
import com.annimon.stream.function.ThrowableFunction;
import com.google.common.base.Stopwatch;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.perf.utils.PerfUtils;

public abstract class Executor<T> {

	protected final Logger logger = LoggerUtils.logger(this.getClass());
	protected final LazySupplier<String> namespace = LazySupplier.wrap(() -> PerfUtils.N_executor + Optional
			.ofNullable(Executor.this.getClass().getName()).map(n -> n.substring(n.lastIndexOf('.') + 1)).orElse(""));
	protected final LazySupplier<String> tag = LazySupplier
			.wrap(() -> Optional.ofNullable(tag()).map(s -> s + ".").orElse(""));

	public final <X extends Throwable> void execute(ThrowableConsumer<T, X> handler) throws X {
		execute(rs -> {
			handler.accept(rs);
			return null;
		});
	}

	public final <X extends Throwable> void execute(ThrowableConsumer<T, X> handler, String extra) throws X {
		execute(rs -> {
			handler.accept(rs);
			return null;
		}, extra);
	}

	public final <X extends Throwable> void execute(ThrowableConsumer<T, X> handler, String[] extras) throws X {
		execute(rs -> {
			handler.accept(rs);
			return null;
		}, extras);
	}

	public final <R, X extends Throwable> R execute(ThrowableFunction<T, R, X> handler) throws X {
		return execute(handler, (String[]) null);
	}

	public final <R, X extends Throwable> R execute(ThrowableFunction<T, R, X> handler, String extra) throws X {
		return execute(handler, new String[] { extra });
	}

	@SuppressWarnings("unchecked")
	public final <R, X extends Throwable> R execute(ThrowableFunction<T, R, X> handler, String[] extras) throws X {
		Stopwatch stopwatch = Stopwatch.createStarted();
		extras = Optional.ofNullable(extras).orElseGet(() -> new String[0]);
		T bean = null;
		X x = null;
		try {
			bean = this.bean();
			PerfUtils.perf(namespace.get(), tag.get() + "bean", extras).count(1)
					.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
			init(bean);
			PerfUtils.perf(namespace.get(), tag.get() + "init", extras).count(1)
					.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
			R rtn = handler.apply(bean);
			PerfUtils.perf(namespace.get(), tag.get() + "exec", extras).count(1)
					.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
			return rtn;
		} catch (Throwable t) {
			PerfUtils.perf(namespace.get(), tag.get() + t.getClass().getSimpleName(), extras).count(1)
					.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
			x = (X) t;
			throw x;
		} finally {
			try {
				if (bean != null) {
					this.close(bean, x);
				}
			} finally {
				PerfUtils.perf(namespace.get(), tag.get() + "done", extras).count(1)
						.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
			}
		}

	}

	protected abstract <X extends Throwable> T bean() throws X;

	protected <X extends Throwable> void init(T bean) throws X {

	}

	protected <X extends Throwable> void close(T bean, X e) {

	}

	protected String tag() {
		return null;
	}

}
