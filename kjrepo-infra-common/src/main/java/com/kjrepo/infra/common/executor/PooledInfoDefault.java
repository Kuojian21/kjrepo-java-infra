package com.kjrepo.infra.common.executor;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class PooledInfoDefault<T> implements PooledInfo<T> {

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
