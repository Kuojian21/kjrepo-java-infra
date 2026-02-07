package com.kjrepo.infra.cluster.selector;

import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;

import com.kjrepo.infra.cluster.instance.Instance;
import com.kjrepo.infra.common.logger.LoggerUtils;

public interface Selector<R> {

	Logger logger = LoggerUtils.logger(Selector.class);

	Instance<R> select(Long key);

	default Instance<R> select() {
		return select(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
	}
}
