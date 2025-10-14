package com.kjrepo.infra.runner.sch;

import java.util.concurrent.TimeUnit;

import com.kjrepo.infra.runner.Runner;

public interface SchRunner extends Runner {

	default boolean isConcurrentRunning() {
		return false;
	}

	default long elapsedWarnThreshold() {
		return TimeUnit.MINUTES.toMillis(1);
	}

}
