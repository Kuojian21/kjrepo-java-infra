package com.kjrepo.infra.register.resource;

import java.util.concurrent.ConcurrentMap;

import com.annimon.stream.Optional;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.utils.TypeMapperUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;

@SuppressWarnings("unchecked")
class IResourceHolder {

	private static final ConcurrentMap<IResource<?, ?>, LazySupplier<?>> resources = Maps.newConcurrentMap();

	static <I, R> R get(IResource<I, R> info) {
		LazySupplier<R> resource = (LazySupplier<R>) resources.get(info);
		if (resource == null) {
			Register<I> register = RegisterFactory.getContext(info.getClass()).getRegister((Class<I>) TypeMapperUtils
					.mapper(info.getClass()).get(IResource.class).get(IResource.class.getTypeParameters()[0]));
			if (resources.putIfAbsent(info, LazySupplier.wrap(() -> {
				return info.mapper().apply(register.get(info.ID()));
			})) == null) {
				register.addListener(info.ID(), event -> {
					refresh(info);
				});
			}
			resource = (LazySupplier<R>) resources.get(info);
		}
		return resource.get();
	}

	static <I, R> void refresh(IResource<I, R> info) {
		Optional.ofNullable(resources.get(info)).ifPresent(lr -> lr.refresh(r -> {
			if (r instanceof AutoCloseable) {
				((AutoCloseable) r).close();
			}
		}));
	}

}
