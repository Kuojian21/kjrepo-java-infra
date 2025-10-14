package com.kjrepo.infra.cluster.resource;

import java.io.Closeable;
import java.util.concurrent.ConcurrentMap;

import com.annimon.stream.function.Function;
import com.google.common.collect.Maps;
import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.cluster.ClusterFactory;
import com.kjrepo.infra.cluster.ClusterInfo;
import com.kjrepo.infra.cluster.instance.InstanceInfo;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;
import com.kjrepo.infra.text.json.utils.TypeMapperUtils;

public interface ClusterResource<R, I> {

	String ID();

	Function<InstanceInfo<I>, R> mapper();

	ConcurrentMap<Object, LazySupplier<?>> resources = Maps.newConcurrentMap();

	@SuppressWarnings({ "unchecked" })
	default Cluster<R> getResource() {
		LazySupplier<Cluster<R>> resource = (LazySupplier<Cluster<R>>) resources.get(this);
		if (resource == null) {
			Class<R> rclazz = (Class<R>) TypeMapperUtils.mapper(getClass()).get(ClusterResource.class)
					.get(ClusterResource.class.getTypeParameters()[0]);
			Class<?> cclazz = (Class<?>) TypeMapperUtils.mapper(getClass()).get(ClusterResource.class)
					.get(ClusterResource.class.getTypeParameters()[2]);
			Register<ClusterInfo<?>> register = (Register<ClusterInfo<?>>) RegisterFactory.getContext(getClass())
					.getRegister(cclazz);
			resource = (LazySupplier<Cluster<R>>) resources.computeIfAbsent(this,
					k -> LazySupplier.wrap(() -> (Cluster<R>) ClusterFactory.cluster(rclazz, register, ID(), mapper(),
							res -> close((R) res))));

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
