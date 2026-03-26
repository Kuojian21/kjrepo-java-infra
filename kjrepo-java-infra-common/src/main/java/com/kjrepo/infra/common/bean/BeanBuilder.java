package com.kjrepo.infra.common.bean;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BeanBuilder<T> {

	public static <T> BeanBuilder<T> builder(T bean) {
		return new BeanBuilder<T>(bean);
	}

	public static <T> BeanBuilder<T> builder(Supplier<T> bean) {
		return new BeanBuilder<T>(bean.get());
	}

	protected final T bean;

	public BeanBuilder(T bean) {
		super();
		this.bean = bean;
	}

	public final BeanBuilder<T> accept(Consumer<T> consumer) {
		consumer.accept(bean);
		return this;
	}

	public final T build() {
		return bean;
	}

}
