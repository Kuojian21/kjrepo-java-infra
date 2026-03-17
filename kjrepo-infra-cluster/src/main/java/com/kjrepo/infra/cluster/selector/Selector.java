package com.kjrepo.infra.cluster.selector;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;

import com.kjrepo.infra.cluster.instance.Instance;
import com.kjrepo.infra.common.logger.LoggerUtils;

public interface Selector {

	Logger logger = LoggerUtils.logger(Selector.class);

	<R> Instance<R> select(List<Instance<R>> instances, Long key);

	default <R> Instance<R> select(List<Instance<R>> instances) {
		return select(instances, ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
	}
}
