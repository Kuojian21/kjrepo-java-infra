package com.kjrepo.infra.network.capture;

import org.openqa.selenium.WebDriver;

import com.kjrepo.infra.common.lazy.LazyRunnable;
import com.kjrepo.infra.executor.pool.PoolExecutor;

import io.github.bonigarcia.wdm.WebDriverManager;

public abstract class Capture<T extends WebDriver, I extends CaptureInfo<T>> extends PoolExecutor<T, I> {

	public static void driver_chrome() {
		driver_chrome.run();
	}

	public static void driver_firefox() {
		driver_firefox.run();
	}

	public static void driver_edge() {
		driver_edge.run();
	}

	private static final LazyRunnable driver_chrome = LazyRunnable.wrap(() -> {
		WebDriverManager.chromedriver().setup();
	});
	private static final LazyRunnable driver_firefox = LazyRunnable.wrap(() -> {
		WebDriverManager.firefoxdriver().setup();
	});
	private static final LazyRunnable driver_edge = LazyRunnable.wrap(() -> {
		WebDriverManager.edgedriver().setup();
	});

	protected Capture(I info) {
		super(info);
	}

	@Override
	protected void destroy(T driver) {
		driver.quit();
	}

}
