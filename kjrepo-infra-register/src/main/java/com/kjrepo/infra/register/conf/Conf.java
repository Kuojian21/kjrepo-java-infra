package com.kjrepo.infra.register.conf;

import java.util.concurrent.ConcurrentMap;

import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.register.Register;

public abstract class Conf<T> {

	public static <T, V> Conf<T> conf(Register<V> register, Function<V, T> mapper, Consumer<T> release) {
		return new Xkconf<>(register, mapper, release);
	}

	private final ConcurrentMap<String, LazySupplier<T>> confs = Maps.newConcurrentMap();

	public <V> Conf() {
		super();
	}

	public T get(String id) {
		LazySupplier<T> conf = confs.get(id);
		if (conf == null) {
			if (confs.putIfAbsent(id, LazySupplier.wrap(() -> {
				return mapper().apply(register().get(id));
			})) == null) {
				/*
				 * Exception in thread "File-Monitor" java.util.ConcurrentModificationException
				 */
				register().addListener(id, event -> {
					T data = confs.get(id).get();
					confs.get(id).refresh();
					if (release() != null) {
						release().accept(data);
					}
				});
			}
		}
		return confs.get(id).get();
	}

	public abstract <V> Register<V> register();

	public abstract <V> Function<V, T> mapper();

	public abstract <V> Consumer<T> release();

	@SuppressWarnings("unchecked")
	static class Xkconf<T, V> extends Conf<T> {

		private final Register<V> register;
		private final Function<V, T> mapper;
		private final Consumer<T> release;

		Xkconf(Register<V> register, Function<V, T> mapper, Consumer<T> release) {
			super();
			this.register = register;
			this.mapper = mapper;
			this.release = release;
		}

		@Override
		public Register<V> register() {
			return this.register;
		}

		@Override
		public Function<V, T> mapper() {
			return this.mapper;
		}

		@Override
		public Consumer<T> release() {
			return this.release;
		}

	}

}
