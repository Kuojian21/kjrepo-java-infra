package com.kjrepo.infra.executor.pool;

import java.util.concurrent.TimeUnit;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

import com.annimon.stream.Optional;
import com.google.common.base.Stopwatch;
import com.kjrepo.infra.common.hook.HookHelper;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.executor.info.InfoExecutor;
import com.kjrepo.infra.perf.utils.PerfUtils;

public abstract class PoolExecutor<T, I extends PoolExecutorInfo<T>> extends InfoExecutor<T, I>
		implements AutoCloseable {

	private final LazySupplier<GenericObjectPool<T>> pool;

	protected PoolExecutor(I info) {
		super(info);
		if (info.getPoolConfig() == null) {
			this.pool = LazySupplier.wrap(() -> null);
		} else {
			this.pool = LazySupplier.wrap(() -> {
				return new GenericObjectPool<T>(new BasePooledObjectFactory<T>() {
					@Override
					public T create() throws Exception {
						Stopwatch stopwatch = Stopwatch.createStarted();
						try {
							T obj = (T) PoolExecutor.this.create();
							PerfUtils.perf(namespace.get(), tag.get() + "create").count(1)
									.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
							return obj;
						} catch (Exception e) {
							PerfUtils.perf(namespace.get(), tag.get() + e.getClass().getSimpleName()).count(1)
									.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
							throw e;
						}
					}

					@Override
					public PooledObject<T> wrap(T obj) {
						return new DefaultPooledObject<>(obj);
					}

					@Override
					public void destroyObject(final PooledObject<T> obj) throws Exception {
						Stopwatch stopwatch = Stopwatch.createStarted();
						try {
							destroy(obj.getObject());
							PerfUtils.perf(namespace.get(), tag.get() + "destroy").count(1)
									.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
						} catch (Exception e) {
							PerfUtils.perf(namespace.get(), tag.get() + e.getClass().getSimpleName()).count(1)
									.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
							throw e;
						}
					}
				}, info.getPoolConfig());
			});
			HookHelper.addHook(
					Optional.of(this.getClass().getName())
							.map(n -> n.substring(Math.max(n.lastIndexOf("."), n.lastIndexOf("$")) + 1)).get(),
					() -> close());
		}
	}

	@Override
	protected final T bean() {
		try {
			if (this.pool.get() == null) {
				return create();
			} else {
				return this.pool.get().borrowObject();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected final <X extends Throwable> void close(T bean, X exception) {
		try {
			if (this.pool.get() == null) {
				this.destroy(bean);
			} else {
				if (validate(bean, exception)) {
					this.after(bean);
					this.pool.get().returnObject(bean);
				} else {
					this.pool.get().invalidateObject(bean);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract T create() throws Exception;

	protected void after(T bean) {

	}

	protected boolean validate(T bean) {
		return true;
	}

	protected <X extends Throwable> boolean validate(T bean, X exception) {
		return exception == null && validate(bean);
	}

	protected void destroy(T bean) throws Exception {
		if (bean != null && bean instanceof AutoCloseable) {
			try {
				((AutoCloseable) bean).close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void close() throws Exception {
		this.pool.refresh(p -> {
			Optional.ofNullable(p).ifPresent(GenericObjectPool::close);
		});
	}

}
