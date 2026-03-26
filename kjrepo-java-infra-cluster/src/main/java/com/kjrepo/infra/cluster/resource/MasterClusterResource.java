package com.kjrepo.infra.cluster.resource;

import org.slf4j.Logger;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.cluster.MasterCluster;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.cluster.info.MasterClusterInfo;
import com.kjrepo.infra.common.logger.LoggerUtils;

public interface MasterClusterResource<R, I, C extends MasterClusterInfo<I>> {

	Logger logger = LoggerUtils.logger(MasterClusterResource.class);

	String ID();

	Function<InstanceInfo<I>, R> mapper();

	default MasterCluster<R> getResource() {
		return MasterClusterResourceHolder.get(this);
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
