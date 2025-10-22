package com.kjrepo.infra.cluster.resource;

import java.io.Closeable;
import java.util.concurrent.ConcurrentMap;

import com.annimon.stream.function.Function;
import com.google.common.collect.Maps;
import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.cluster.ClusterFactory;
import com.kjrepo.infra.cluster.ClusterInfo;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.text.json.utils.TypeMapperUtils;

public interface ClusterResource<R, I, C extends ClusterInfo<I>> {

	String ID();

	Function<I, R> mapper();

	ConcurrentMap<Object, LazySupplier<?>> resources = Maps.newConcurrentMap();

	@SuppressWarnings({ "unchecked" })
	default Cluster<R> getResource() {
		LazySupplier<Cluster<R>> resource = (LazySupplier<Cluster<R>>) resources.get(this);
		if (resource == null) {
			Class<R> rclazz = (Class<R>) TypeMapperUtils.mapper(getClass()).get(ClusterResource.class)
					.get(ClusterResource.class.getTypeParameters()[0]);
			Class<C> cclazz = (Class<C>) TypeMapperUtils.mapper(getClass()).get(ClusterResource.class)
					.get(ClusterResource.class.getTypeParameters()[2]);
			resource = (LazySupplier<Cluster<R>>) resources.computeIfAbsent(this, k -> ClusterFactory.cluster(rclazz,
					cclazz, ID(), info -> mapper().apply(info.getInfo()), res -> close((R) res)));

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
