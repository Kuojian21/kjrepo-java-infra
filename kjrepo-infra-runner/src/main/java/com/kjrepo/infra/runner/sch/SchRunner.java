package com.kjrepo.infra.runner.sch;

import com.kjrepo.infra.runner.Runner;

public interface SchRunner extends Runner {

	default boolean isConcurrentRunning() {
		return false;
	}

}
