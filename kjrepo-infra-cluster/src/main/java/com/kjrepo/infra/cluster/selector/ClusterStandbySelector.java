package com.kjrepo.infra.cluster.selector;

import com.kjrepo.infra.cluster.standby.Standby;

public interface ClusterStandbySelector<R> {

	Standby<R> select(Object... args);

}
