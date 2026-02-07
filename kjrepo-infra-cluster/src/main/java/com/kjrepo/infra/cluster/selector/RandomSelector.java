package com.kjrepo.infra.cluster.selector;

import java.util.List;

import com.google.common.collect.Lists;
import com.kjrepo.infra.cluster.instance.Instance;
import com.kjrepo.infra.common.lazy.LazySupplier;

public class RandomSelector<R> extends AbstractSelector<R> {

	public RandomSelector(List<LazySupplier<Instance<R>>> instances) {
		super(instances);
	}

	@Override
	public Instance<R> select(Long key) {
		List<LazySupplier<Instance<R>>> list = Lists.newArrayList(super.instances);
		while (list.size() > 0) {
			LazySupplier<Instance<R>> instance = list.get((int) (key % list.size()));
			if (instance.get() == null) {
				logger.warn("The instance is NULL!!!");
				list.remove(instance);
			} else {
				return instance.get();
			}
		}
		return null;
	}

}
