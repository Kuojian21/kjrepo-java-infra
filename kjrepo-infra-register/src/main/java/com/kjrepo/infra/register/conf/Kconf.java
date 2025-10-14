package com.kjrepo.infra.register.conf;

import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.kjrepo.infra.register.Register;

@SuppressWarnings("unchecked")
public class Kconf<T, V> extends Conf<T> {

	public static <T, V> Conf<T> conf(Register<V> register, Function<V, T> mapper, Consumer<T> release) {
		return new Kconf<>(register, mapper, release);
	}

	private final Register<V> register;
	private final Function<V, T> mapper;
	private final Consumer<T> release;

	public Kconf(Register<V> register, Function<V, T> mapper, Consumer<T> release) {
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
