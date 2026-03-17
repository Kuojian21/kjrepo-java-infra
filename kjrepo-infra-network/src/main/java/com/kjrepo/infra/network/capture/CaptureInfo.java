package com.kjrepo.infra.network.capture;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.openqa.selenium.WebDriver;

import com.kjrepo.infra.executor.pool.PoolExecutorInfo;

public class CaptureInfo<T extends WebDriver> implements PoolExecutorInfo<T> {

	private GenericObjectPoolConfig<T> poolConfig;

	@Override
	public GenericObjectPoolConfig<T> getPoolConfig() {
		return this.poolConfig;
	}

	@Override
	public void setPoolConfig(GenericObjectPoolConfig<T> poolConfig) {
		this.poolConfig = poolConfig;
	}
}
