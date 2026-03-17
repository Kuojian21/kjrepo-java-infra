package com.kjrepo.infra.cluster.resource;

import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;

import com.annimon.stream.function.Function;
import com.google.common.collect.Maps;
import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.cluster.impl.ClusterFactory;
import com.kjrepo.infra.cluster.info.ClusterInfo;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.text.json.utils.TypeMapperUtils;

public interface ClusterResource<R, I, C extends ClusterInfo<I>> {

	Logger logger = LoggerUtils.logger(ClusterResource.class);

	String ID();

	Function<InstanceInfo<I>, R> mapper();

	ConcurrentMap<Object, LazySupplier<?>> resources = Maps.newConcurrentMap();

	@SuppressWarnings({ "unchecked" })
	default Cluster<R> getResource() {
		LazySupplier<Cluster<R>> resource = (LazySupplier<Cluster<R>>) resources.get(this);
		if (resource == null) {
			Class<C> cclazz = (Class<C>) TypeMapperUtils.mapper(getClass()).get(ClusterResource.class)
					.get(ClusterResource.class.getTypeParameters()[2]);
			resource = (LazySupplier<Cluster<R>>) resources.computeIfAbsent(this, k -> LazySupplier
					.wrap(() -> ClusterFactory.cluster(cclazz, ID(), mapper(), ClusterResource.this::close)));
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
