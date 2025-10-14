package com.kjrepo.infra.common.executor;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public interface PooledInfo<T> {

	GenericObjectPoolConfig<T> getPoolConfig();

	void setPoolConfig(GenericObjectPoolConfig<T> poolConfig);

}
