package com.kjrepo.infra.cluster.resource;

import java.io.Closeable;
import java.util.concurrent.ConcurrentMap;

import com.annimon.stream.function.Function;
import com.google.common.collect.Maps;
import com.kjrepo.infra.cluster.standby.Standby;
import com.kjrepo.infra.cluster.standby.StandbyFactory;
import com.kjrepo.infra.cluster.standby.StandbyInfo;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.text.json.utils.TypeMapperUtils;

public interface StandbyResource<R, I, C extends StandbyInfo<I>> {

	String ID();

	Function<I, R> mapper();

	ConcurrentMap<Object, LazySupplier<?>> resources = Maps.newConcurrentMap();

	@SuppressWarnings("unchecked")
	default Standby<R> getResource() {
		LazySupplier<Standby<R>> resource = (LazySupplier<Standby<R>>) resources.get(this);
		if (resource == null) {
			Class<R> rclazz = (Class<R>) TypeMapperUtils.mapper(getClass()).get(StandbyResource.class)
					.get(StandbyResource.class.getTypeParameters()[0]);
			Class<C> cclazz = (Class<C>) TypeMapperUtils.mapper(getClass()).get(StandbyResource.class)
					.get(StandbyResource.class.getTypeParameters()[2]);
			resource = (LazySupplier<Standby<R>>) resources.computeIfAbsent(this,
					k -> StandbyFactory.standby(rclazz, cclazz, ID(), mapper(), res -> close((R) res)));

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
