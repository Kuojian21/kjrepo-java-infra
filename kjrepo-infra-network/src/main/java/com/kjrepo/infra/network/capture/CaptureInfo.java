package com.kjrepo.infra.network.capture;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.openqa.selenium.chrome.ChromeDriver;

import com.kjrepo.infra.common.executor.PooledInfo;
import com.kjrepo.infra.common.lazy.LazySupplier;

public class CaptureInfo implements PooledInfo<ChromeDriver> {

	public static final LazySupplier<CaptureInfo> DEFAULT = LazySupplier.wrap(() -> {
		CaptureInfo info = new CaptureInfo();
		GenericObjectPoolConfig<ChromeDriver> config = new GenericObjectPoolConfig<ChromeDriver>();
		config.setMinIdle(0);
		config.setMaxTotal(Runtime.getRuntime().availableProcessors());
		info.setPoolConfig(config);
		return info;
	});

	private GenericObjectPoolConfig<ChromeDriver> poolConfig;

	@Override
	public GenericObjectPoolConfig<ChromeDriver> getPoolConfig() {
		return this.poolConfig;
	}

	@Override
	public void setPoolConfig(GenericObjectPoolConfig<ChromeDriver> poolConfig) {
		this.poolConfig = poolConfig;
	}

}