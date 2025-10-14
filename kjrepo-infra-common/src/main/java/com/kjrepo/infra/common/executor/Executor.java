package com.kjrepo.infra.common.executor;

import org.slf4j.Logger;

import com.annimon.stream.function.ThrowableConsumer;
import com.annimon.stream.function.ThrowableFunction;
import com.kjrepo.infra.common.logger.LoggerUtils;

public abstract class Executor<T> {

	protected final Logger logger = LoggerUtils.logger(this.getClass());

	public final <E extends Throwable> void execute(ThrowableConsumer<T, E> handler) throws E {
		execute(rs -> {
			handler.accept(rs);
			return null;
		});
	}

	@SuppressWarnings("unchecked")
	public final <R, E extends Throwable> R execute(ThrowableFunction<T, R, E> handler) throws E {
		E e = null;
		T bean = this.bean();
		try {
			this.init(bean);
			R rtn = handler.apply(bean);
//			this.after(bean);
			return rtn;
		} catch (Throwable t) {
			e = (E) t;
//			try {
//				this.after(bean);
//			} catch (Exception e1) {
//				logger.error("", e1);
//			}
			throw e;
		} finally {
			this.close(bean, e);
		}
	}

	protected abstract T bean();

	protected void init(T bean) {

	}

//	protected void after(T bean) {
//
//	}

	protected <E extends Throwable> void close(T bean, E e) {

	}

}
