package com.kjrepo.infra.register.conf;

import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;
import com.annimon.stream.function.ThrowableConsumer;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;

public abstract class Kconf<T> implements Supplier<T> {

	public static <T> Kconf<T> conf(String key, Class<?> clazz) {
		return conf(key, clazz, (T) null);
	}

	@SuppressWarnings("unchecked")
	public static <T> Kconf<T> conf(String key, Class<?> clazz, T defValue) {
		return conf(key, clazz, obj -> obj == null ? defValue : (T) obj, null);
	}

	public static <T, V> Kconf<T> conf(String key, Class<V> clazz, Function<V, T> mapper) {
		return conf(key, clazz, mapper, null);
	}

	public static <T, V> Kconf<T> conf(String key, Class<V> clazz, Function<V, T> mapper,
			ThrowableConsumer<T, Throwable> release) {
		return conf(key, (Register<V>) RegisterFactory.getContext().getRegister(clazz), mapper, release);
	}

	public static <T, V> Kconf<T> conf(String key, Register<V> register, Function<V, T> mapper,
			ThrowableConsumer<T, Throwable> release) {
		return new XKconf<>(key, register, mapper, release);
	}

	private final LazySupplier<T> conf;
	private final String key;

	public <V> Kconf(String key, Register<V> register, Function<V, T> mapper, ThrowableConsumer<T, Throwable> release) {
		super();
		this.key = key;
		this.conf = LazySupplier.wrap(() -> {
			return mapper.apply((V) register.get(key));
		});
		register.addListener(key, event -> {
			T data = conf.get();
			conf.refresh();
			if (release != null) {
				try {
					release.accept(data);
				} catch (Throwable e) {
					LoggerUtils.logger(Kconf.class).error("", e);
				}
			}
		});
	}

	public String key() {
		return this.key;
	}

	@Override
	public T get() {
		return this.conf.get();
	}

	static class XKconf<T, V> extends Kconf<T> {
		XKconf(String key, Register<V> register, Function<V, T> mapper, ThrowableConsumer<T, Throwable> release) {
			super(key, register, mapper, release);
		}
	}

}
