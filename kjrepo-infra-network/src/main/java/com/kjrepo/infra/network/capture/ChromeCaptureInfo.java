package com.kjrepo.infra.network.capture;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.openqa.selenium.chrome.ChromeDriver;

import com.kjrepo.infra.common.lazy.LazySupplier;

public class ChromeCaptureInfo extends CaptureInfo<ChromeDriver> {

	public static final LazySupplier<ChromeCaptureInfo> DEFAULT = LazySupplier.wrap(() -> {
		ChromeCaptureInfo info = new ChromeCaptureInfo();
		GenericObjectPoolConfig<ChromeDriver> config = new GenericObjectPoolConfig<ChromeDriver>();
		config.setMinIdle(0);
		config.setMaxTotal(Runtime.getRuntime().availableProcessors());
		info.setPoolConfig(config);
		return info;
	});

}