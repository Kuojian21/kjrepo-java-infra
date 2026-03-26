package com.kjrepo.infra.cluster.resource;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.kjrepo.infra.cluster.Master;
import com.kjrepo.infra.cluster.impl.MasterFactory;
import com.kjrepo.infra.cluster.info.MasterInfo;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.utils.TypeMapperUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;

@SuppressWarnings("unchecked")
class MasterResourceHolder {

	static final ConcurrentMap<MasterResource<?, ?, ?>, LazySupplier<?>> resources = Maps.newConcurrentMap();

	static <R, I, C extends MasterInfo<I>> Master<R> get(MasterResource<R, I, C> info) {
		return (Master<R>) resources.computeIfAbsent(info, k -> LazySupplier.wrap(() -> {
			Register<C> register = RegisterFactory.getContext(info.getClass())
					.getRegister((Class<C>) TypeMapperUtils.mapper(info.getClass()).get(MasterResource.class)
							.get(MasterResource.class.getTypeParameters()[2]));
			return MasterFactory.master(register, info.ID(), info.mapper(), info::close);
		})).get();
	}

}
