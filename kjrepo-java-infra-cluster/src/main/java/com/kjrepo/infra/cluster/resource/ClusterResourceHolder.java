package com.kjrepo.infra.cluster.resource;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.cluster.impl.ClusterFactory;
import com.kjrepo.infra.cluster.info.ClusterInfo;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.utils.TypeMapperUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;

@SuppressWarnings("unchecked")
class ClusterResourceHolder {

	static final ConcurrentMap<ClusterResource<?, ?, ?>, LazySupplier<?>> resources = Maps.newConcurrentMap();

	static <R, I, C extends ClusterInfo<I>> Cluster<R> get(ClusterResource<R, I, C> info) {
		return (Cluster<R>) resources.computeIfAbsent(info, k -> LazySupplier.wrap(() -> {
			Register<C> register = RegisterFactory.getContext(info.getClass())
					.getRegister((Class<C>) TypeMapperUtils.mapper(info.getClass()).get(ClusterResource.class)
							.get(ClusterResource.class.getTypeParameters()[2]));
			return ClusterFactory.cluster(register, info.ID(), info.mapper(), info::close);
		})).get();
	}

}
