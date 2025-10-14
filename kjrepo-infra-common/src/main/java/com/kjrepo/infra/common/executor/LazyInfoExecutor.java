package com.kjrepo.infra.common.executor;

import java.io.Closeable;
import java.io.IOException;

import com.annimon.stream.function.Supplier;
import com.kjrepo.infra.common.lazy.LazySupplier;

public class LazyInfoExecutor<T, I> extends InfoExecutor<T, I> implements Closeable {

	private final LazySupplier<T> supplier;

	protected LazyInfoExecutor(I info, Supplier<T> supplier) {
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
	public void close() throws IOException {
		if (this.supplier.isInited()) {
			this.destroy(this.bean());
		}
	}

}
