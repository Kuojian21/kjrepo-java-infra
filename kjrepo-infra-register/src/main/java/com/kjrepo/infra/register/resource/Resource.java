package com.kjrepo.infra.register.resource;

import java.io.Closeable;
import java.util.concurrent.ConcurrentMap;

import com.annimon.stream.function.Function;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;

public interface Resource<R> {

	String ID();

	Function<?, R> mapper();

	Class<?> iclazz();

	ConcurrentMap<Object, LazySupplier<?>> resources = Maps.newConcurrentMap();

	@SuppressWarnings("unchecked")
	default <I> R get() {
		LazySupplier<R> resource = (LazySupplier<R>) resources.get(this);
		if (resource == null) {
			Register<I> register = (Register<I>) RegisterFactory.getContext(getClass()).getRegister(iclazz());
			if (resources.putIfAbsent(this, LazySupplier.wrap(() -> {
				return ((Function<I, R>) mapper()).apply(register.get(ID()));
			})) == null) {
				register.addListener(ID(), event -> {
					R oResource = (R) resources.get(this).get();
					resources.get(this).refresh();
					close(oResource);
				});
			}
			resource = (LazySupplier<R>) resources.get(this);
		}
		return resource.get();
	}

	default void close(R resource) {
		if (resource != null && resource instanceof Closeable) {
			try {
				((Closeable) resource).close();
			} catch (Exception e) {
				LoggerUtils.logger(getClass()).error("", e);
			}
		}
	}

}
