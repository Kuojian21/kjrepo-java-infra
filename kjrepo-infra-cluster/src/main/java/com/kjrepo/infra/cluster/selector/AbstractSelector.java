package com.kjrepo.infra.cluster.selector;

import java.util.List;

import com.kjrepo.infra.cluster.instance.Instance;
import com.kjrepo.infra.common.lazy.LazySupplier;

public abstract class AbstractSelector<R> implements Selector<R> {

	protected final List<LazySupplier<Instance<R>>> instances;

	public AbstractSelector(List<LazySupplier<Instance<R>>> instances) {
		this.instances = instances;
	}

}
