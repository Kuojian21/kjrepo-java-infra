package com.kjrepo.infra.cluster.selector;

import java.util.List;

import com.kjrepo.infra.cluster.standby.Standby;
import com.kjrepo.infra.common.lazy.LazySupplier;

public abstract class AbstractClusterStandbySelector<R> implements ClusterStandbySelector<R> {

	protected final List<LazySupplier<Standby<R>>> instances;

	public AbstractClusterStandbySelector(List<LazySupplier<Standby<R>>> instances) {
		this.instances = instances;
	}

}
