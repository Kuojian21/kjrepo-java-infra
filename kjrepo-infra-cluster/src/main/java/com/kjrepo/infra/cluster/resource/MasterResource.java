package com.kjrepo.infra.cluster.resource;

import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;

import com.annimon.stream.function.Function;
import com.google.common.collect.Maps;
import com.kjrepo.infra.cluster.Master;
import com.kjrepo.infra.cluster.impl.MasterFactory;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.cluster.info.MasterInfo;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.text.json.utils.TypeMapperUtils;

public interface MasterResource<R, I, C extends MasterInfo<I>> {

	Logger logger = LoggerUtils.logger(MasterResource.class);

	String ID();

	Function<InstanceInfo<I>, R> mapper();

	ConcurrentMap<Object, LazySupplier<?>> resources = Maps.newConcurrentMap();

	@SuppressWarnings("unchecked")
	default Master<R> getResource() {
		LazySupplier<Master<R>> resource = (LazySupplier<Master<R>>) resources.get(this);
		if (resource == null) {
			Class<C> cclazz = (Class<C>) TypeMapperUtils.mapper(MasterResource.this.getClass())
					.get(MasterResource.class).get(MasterResource.class.getTypeParameters()[2]);
			resource = (LazySupplier<Master<R>>) resources.computeIfAbsent(this, k -> LazySupplier
					.wrap(() -> MasterFactory.master(cclazz, ID(), mapper(), MasterResource.this::close)));

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
