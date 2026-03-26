package com.kjrepo.infra.executor.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public abstract class AbstractPoolExecutorInfo<T> implements PoolExecutorInfo<T> {

	private GenericObjectPoolConfig<T> poolConfig;

	@Override
	public GenericObjectPoolConfig<T> getPoolConfig() {
		return poolConfig;
	}

	@Override
	public void setPoolConfig(GenericObjectPoolConfig<T> poolConfig) {
		this.poolConfig = poolConfig;
	}

}
