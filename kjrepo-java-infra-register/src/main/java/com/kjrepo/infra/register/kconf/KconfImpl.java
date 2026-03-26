package com.kjrepo.infra.register.kconf;

import com.annimon.stream.function.Function;
import com.annimon.stream.function.ThrowableConsumer;
import com.kjrepo.infra.common.hook.HookHelper;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.utils.StackUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;

class KconfImpl<T, V> implements Kconf<T> {
	private final String key;
	private final Class<V> clazz;
	private final Function<V, T> mapper;
	private final ThrowableConsumer<T, Throwable> release;
	private final LazySupplier<LazySupplier<T>> conf;

	public KconfImpl(String key, Class<V> clazz, Function<V, T> mapper, ThrowableConsumer<T, Throwable> release) {
		super();
		this.key = key;
		this.clazz = clazz;
		this.mapper = mapper;
		this.release = release;
		String classname = StackUtils.firstBusinessInvokerClassname();
		this.conf = LazySupplier.wrap(() -> {
			Register<V> register = RegisterFactory.getContext(classname).getRegister(this.clazz);
			register.addListener(key, event -> KconfImpl.this.refresh());
			if (KconfImpl.this.release != null) {
				HookHelper.addHook("kconf", KconfImpl.this::refresh);
			}
			return LazySupplier.wrap(() -> this.mapper.apply(register.get(key)));
		});
	}

	public String key() {
		return this.key;
	}

	@Override
	public T get() {
		return this.conf.get().get();
	}

	public void close() {
		this.refresh();
	}

	private void refresh() {
		this.conf.get().refresh(this.release);
	}
}