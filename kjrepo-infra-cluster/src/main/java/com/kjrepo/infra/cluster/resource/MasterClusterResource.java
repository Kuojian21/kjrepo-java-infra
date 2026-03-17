package com.kjrepo.infra.cluster.resource;

import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;

import com.annimon.stream.function.Function;
import com.google.common.collect.Maps;
import com.kjrepo.infra.cluster.MasterCluster;
import com.kjrepo.infra.cluster.impl.MasterClusterFactory;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.cluster.info.MasterClusterInfo;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.text.json.utils.TypeMapperUtils;

public interface MasterClusterResource<R, I, C extends MasterClusterInfo<I>> {

	Logger logger = LoggerUtils.logger(MasterClusterResource.class);

	String ID();

	Function<InstanceInfo<I>, R> mapper();

	ConcurrentMap<Object, LazySupplier<?>> resources = Maps.newConcurrentMap();

	@SuppressWarnings({ "unchecked" })
	default MasterCluster<R> getResource() {
		LazySupplier<MasterCluster<R>> resource = (LazySupplier<MasterCluster<R>>) resources.get(this);
		if (resource == null) {
			Class<C> cclazz = (Class<C>) TypeMapperUtils.mapper(getClass()).get(MasterClusterResource.class)
					.get(MasterClusterResource.class.getTypeParameters()[2]);
			resource = (LazySupplier<MasterCluster<R>>) resources.computeIfAbsent(this, k -> LazySupplier.wrap(
					() -> MasterClusterFactory.cluster(cclazz, ID(), mapper(), MasterClusterResource.this::close)));
		}
		return resource.get();
	}

	default void close(R resource) {
		if (resource != null && resource instanceof AutoCloseable) {
			try {
				((AutoCloseable) resource).close();
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

}
