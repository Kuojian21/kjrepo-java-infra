package com.kjrepo.infra.executor.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public interface PoolExecutorInfo<T> {

	GenericObjectPoolConfig<T> getPoolConfig();

	void setPoolConfig(GenericObjectPoolConfig<T> poolConfig);

}
