package com.kjrepo.infra.cluster.selector;

import java.util.List;

import com.kjrepo.infra.cluster.instance.Instance;

public class RandomSelector implements Selector {

	@Override
	public <R> Instance<R> select(List<Instance<R>> instances, Long key) {
		return instances.size() == 0 ? null : instances.get((int) (key % instances.size()));
	}

}
