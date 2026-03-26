package com.kjrepo.infra.runner.mq;

import com.kjrepo.infra.runner.Runner;

public interface MQRunner extends Runner {

	ITopic topic();

	IGroup group();

	default String ID() {
		return this.topic().topic();
	}

}
