package com.kjrepo.infra.cluster.selector;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.collect.Lists;
import com.kjrepo.infra.cluster.instance.Instance;
import com.kjrepo.infra.common.lazy.LazySupplier;

public class RandomSelector<R> extends AbstractSelector<R> {

	public RandomSelector(List<LazySupplier<Instance<R>>> instances) {
		super(instances);
	}

	@Override
	public Instance<R> select(Object... args) {
		List<LazySupplier<Instance<R>>> list = Lists.newArrayList(super.instances);
		return list.get(ThreadLocalRandom.current().nextInt(list.size())).get();
	}

}
