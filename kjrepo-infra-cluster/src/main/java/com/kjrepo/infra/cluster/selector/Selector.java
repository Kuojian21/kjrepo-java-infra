package com.kjrepo.infra.cluster.selector;

import com.kjrepo.infra.cluster.instance.Instance;

public interface Selector<R> {

	Instance<R> select(Object... args);
}
