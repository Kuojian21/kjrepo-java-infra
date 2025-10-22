package com.kjrepo.infra.common.executor;

import java.io.Closeable;
import java.io.IOException;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.kjrepo.infra.common.term.HookHelper;

public abstract class PooledInfoExecutor<T, I extends PooledInfo<T>> extends InfoExecutor<T, I> implements Closeable {

	private final GenericObjectPool<T> pool;

	protected PooledInfoExecutor(I info) {
		super(info);
		if (info.getPoolConfig() == null) {
			this.pool = null;
		} else {
			this.pool = new GenericObjectPool<T>(new BasePooledObjectFactory<T>() {

				@Override
				public T create() throws Exception {
					return (T) PooledInfoExecutor.this.create();
				}

				@Override
				public PooledObject<T> wrap(T obj) {
					return new DefaultPooledObject<>(obj);
				}

				@Override
				public void destroyObject(final PooledObject<T> obj) throws Exception {
					destroy(obj.getObject());
				}

			}, info.getPoolConfig());
			HookHelper.addHook("pool-info-executor", () -> close());
		}
	}

	@Override
	protected final T bean() {
		try {
			if (this.pool == null) {
				return create();
			} else {
				return this.pool.borrowObject();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected final <E extends Throwable> void close(T bean, E exception) {
		try {
			if (this.pool == null) {
				this.destroy(bean);
			} else {
				if (validate(bean, exception)) {
					this.after(bean);
					this.pool.returnObject(bean);
				} else {
					this.pool.invalidateObject(bean);
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

	protected <E extends Throwable> boolean validate(T bean, E exception) {
		return exception == null && validate(bean);
	}

	protected void destroy(T bean) throws Exception {
		if (bean != null && bean instanceof Closeable) {
			try {
				((Closeable) bean).close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void close() throws IOException {
		if (this.pool != null) {
			this.pool.close();
		}
	}

}
