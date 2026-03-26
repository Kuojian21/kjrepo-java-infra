package com.kjrepo.infra.cluster.resource;

import org.slf4j.Logger;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.cluster.info.ClusterInfo;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.common.logger.LoggerUtils;

public interface ClusterResource<R, I, C extends ClusterInfo<I>> {

	Logger logger = LoggerUtils.logger(ClusterResource.class);

	String ID();

	Function<InstanceInfo<I>, R> mapper();

	default Cluster<R> getResource() {
		return ClusterResourceHolder.get(this);
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
