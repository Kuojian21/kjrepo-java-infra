package com.kjrepo.infra.executor.lazy;

import java.io.Closeable;
import java.io.IOException;

import com.annimon.stream.function.Supplier;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.executor.info.InfoExecutor;

public class LazyExecutor<T, I> extends InfoExecutor<T, I> implements AutoCloseable {

	private final LazySupplier<T> supplier;

	protected LazyExecutor(I info, Supplier<T> supplier) {
		super(info);
		this.supplier = LazySupplier.wrap(supplier);
	}

	@Override
	protected final T bean() {
		return this.supplier.get();
	}

	protected void refresh() {
		T bean = bean();
		this.supplier.refresh();
		destroy(bean);
	}

	protected void destroy(T bean) {
		if (bean != null && bean instanceof Closeable) {
			try {
				((Closeable) bean).close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void close() throws Exception {
		this.supplier.refresh(LazyExecutor.this::destroy);
	}

}
