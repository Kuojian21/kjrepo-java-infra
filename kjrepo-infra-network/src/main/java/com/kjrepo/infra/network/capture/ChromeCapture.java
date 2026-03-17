package com.kjrepo.infra.network.capture;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.Event;
import org.openqa.selenium.devtools.v142.network.Network;

import com.google.common.util.concurrent.Uninterruptibles;
import com.kjrepo.infra.common.utils.RunUtils;

public class ChromeCapture extends Capture<ChromeDriver, ChromeCaptureInfo> {

	public ChromeCapture(ChromeCaptureInfo info) {
		super(info);
	}

	public <T> void capture(String url, Duration timeout, List<Event<?>> events, ChromeCaptureHandler<T> handler)
			throws TimeoutException {
		String host = RunUtils.catching(() -> new URL(url).getHost());
		this.execute(driver -> {
			try {
				driver.getDevTools().createSession();
				driver.getDevTools().send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty(),
						Optional.empty(), Optional.empty()));
				events.forEach(event -> {
					driver.getDevTools().addListener(event, arg -> handler.handle(driver, arg));
				});
				driver.get(url);
				long currentTimeMillis = System.currentTimeMillis();
				do {
					if (handler.isDone()) {
						break;
					} else {
						Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
					}
				} while (!driver.getWindowHandles().isEmpty()
						&& System.currentTimeMillis() - currentTimeMillis <= timeout.toMillis());
				if (handler.isDone()) {
				} else {
					throw new TimeoutException();
				}
			} finally {
				driver.getDevTools().clearListeners();
				driver.resetCooldown();
				driver.resetInputState();
				driver.getDevTools().close();
			}
		}, new String[] { host });
	}

	@Override
	protected ChromeDriver create() throws Exception {
		ChromeDriver driver = new ChromeDriver();
		return driver;
	}

	@Override
	protected void init(ChromeDriver driver) {
		driver.get("data:,");
	}

	@Override
	protected void after(ChromeDriver driver) {
		driver.get("data:,");
	}

}
