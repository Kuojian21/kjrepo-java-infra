package com.kjrepo.infra.cluster.resource;

import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;

import com.annimon.stream.function.Function;
import com.google.common.collect.Maps;
import com.kjrepo.infra.cluster.Master;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.cluster.info.MasterInfo;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;

public interface MasterResource<R, I, C extends MasterInfo<I>> {

	Logger logger = LoggerUtils.logger(MasterResource.class);

	String ID();

	Function<InstanceInfo<I>, R> mapper();

	ConcurrentMap<Object, LazySupplier<?>> resources = Maps.newConcurrentMap();

	default Master<R> getResource() {
		return MasterResourceHolder.get(this);
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
