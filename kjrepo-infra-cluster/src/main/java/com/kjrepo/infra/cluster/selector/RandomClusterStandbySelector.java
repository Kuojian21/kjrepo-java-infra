package com.kjrepo.infra.cluster.selector;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.collect.Lists;
import com.kjrepo.infra.cluster.standby.Standby;
import com.kjrepo.infra.common.lazy.LazySupplier;

public class RandomClusterStandbySelector<R> extends AbstractClusterStandbySelector<R> {

	public RandomClusterStandbySelector(List<LazySupplier<Standby<R>>> instances) {
		super(instances);
	}

	@Override
	public Standby<R> select(Object... args) {
		List<LazySupplier<Standby<R>>> list = Lists.newArrayList(super.instances);
		return list.get(ThreadLocalRandom.current().nextInt(list.size())).get();
	}

}
