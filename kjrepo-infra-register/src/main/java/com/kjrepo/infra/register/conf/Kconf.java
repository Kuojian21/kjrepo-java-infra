package com.kjrepo.infra.register.conf;

import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.utils.StackUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;

public abstract class Kconf<T> implements Supplier<T> {

	public static <T> Kconf<T> conf(String key, Class<?> clazz) {
		return conf(key, clazz, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> Kconf<T> conf(String key, Class<?> clazz, T defValue) {
		return conf(key, clazz, obj -> obj == null ? defValue : (T) obj, null);
	}

	public static <T, V> Kconf<T> conf(String key, Class<V> clazz, Function<V, T> mapper, Consumer<T> release) {
		return conf(key, (Register<V>) RegisterFactory.getContext(StackUtils.firstBusinessInvokerClassname()).getRegister(clazz), mapper,
				release);
	}

	public static <T, V> Kconf<T> conf(String key, Register<V> register, Function<V, T> mapper, Consumer<T> release) {
		return new XKconf<>(key, register, mapper, release);
	}

	private final LazySupplier<T> conf;
	private final String key;

	public <V> Kconf(String key, Register<V> register, Function<V, T> mapper, Consumer<T> release) {
		super();
		this.key = key;
		this.conf = LazySupplier.wrap(() -> {
			return mapper.apply((V) register.get(key));
		});
		register.addListener(key, event -> {
			T data = conf.get();
			conf.refresh();
			if (release != null) {
				release.accept(data);
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

//		private final Register<V> register;
//		private final Function<V, T> mapper;
//		private final Consumer<T> release;

		XKconf(String key, Register<V> register, Function<V, T> mapper, Consumer<T> release) {
			super(key, register, mapper, release);
//			this.register = register;
//			this.mapper = mapper;
//			this.release = release;
		}

//		@Override
//		public Register<V> register() {
//			return this.register;
//		}
//
//		@Override
//		public Function<V, T> mapper() {
//			return this.mapper;
//		}
//
//		@Override
//		public Consumer<T> release() {
//			return this.release;
//		}

	}

//	private final ConcurrentMap<String, LazySupplier<T>> confs = Maps.newConcurrentMap();
//	public T get(String id) {
//		LazySupplier<T> conf = confs.get(id);
//		if (conf == null) {
//			if (confs.putIfAbsent(id, LazySupplier.wrap(() -> {
//				return mapper().apply(register().get(id));
//			})) == null) {
//				/*
//				 * Exception in thread "File-Monitor" java.util.ConcurrentModificationException
//				 */
//				register().addListener(id, event -> {
//					T data = confs.get(id).get();
//					confs.get(id).refresh();
//					if (release() != null) {
//						release().accept(data);
//					}
//				});
//			}
//		}
//		return confs.get(id).get();
//	}
//
//	protected abstract <V> Register<V> register();
//
//	protected abstract <V> Function<V, T> mapper();
//
//	protected abstract <V> Consumer<T> release();
}
