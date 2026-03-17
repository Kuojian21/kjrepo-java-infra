package com.kjrepo.infra.register.resource;

import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;

import com.annimon.stream.Optional;
import com.annimon.stream.function.Function;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;
import com.kjrepo.infra.text.json.utils.TypeMapperUtils;

@SuppressWarnings("unchecked")
public interface IResource<I, R> {

	Logger logger = LoggerUtils.logger(IResource.class);

	String ID();

	Function<I, R> mapper();

	ConcurrentMap<IResource<?, ?>, LazySupplier<?>> resources = Maps.newConcurrentMap();

	default R get() {
		LazySupplier<R> resource = (LazySupplier<R>) resources.get(this);
		if (resource == null) {
			Class<I> clazz = (Class<I>) TypeMapperUtils.mapper(getClass()).get(IResource.class)
					.get(IResource.class.getTypeParameters()[0]);
			Register<I> register = (Register<I>) RegisterFactory.getContext(getClass()).getRegister(clazz);
			if (resources.putIfAbsent(this, LazySupplier.wrap(() -> {
				return ((Function<I, R>) mapper()).apply(register.get(ID()));
			})) == null) {
				register.addListener(ID(), event -> {
					refresh();
				});
			}
			resource = (LazySupplier<R>) resources.get(this);
		}
		return resource.get();
	}

	default void refresh() {
		Optional.ofNullable(resources.get(this)).ifPresent(lr -> lr.refresh(r -> {
			if (r instanceof AutoCloseable) {
				((AutoCloseable) r).close();
			}
		}));
	}

}
