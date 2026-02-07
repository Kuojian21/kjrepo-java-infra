package com.kjrepo.infra.network.capture;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.Event;
import org.openqa.selenium.devtools.v142.network.Network;
import org.openqa.selenium.devtools.v142.network.model.RequestWillBeSent;
import org.slf4j.Logger;

import com.annimon.stream.IntStream;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import com.kjrepo.infra.common.executor.PooledInfoExecutor;
import com.kjrepo.infra.common.lazy.LazyRunnable;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.number.N_humanUtils;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Capture extends PooledInfoExecutor<ChromeDriver, CaptureInfo> {

	public static final LazySupplier<Capture> DEFAULT = LazySupplier.wrap(() -> new Capture(CaptureInfo.DEFAULT.get()));

	private static final Logger logger = LoggerUtils.logger(Capture.class);

	public static void driver_manager() {
		driver_manager.run();
	}

	public Capture(CaptureInfo info) {
		super(info);
	}

	public void capture(String url, long timeout, TimeUnit timeunit, List<Event<?>> events, CaptureHandler handler)
			throws TimeoutException {
		this.<TimeoutException>execute(driver -> {
			events.forEach(event -> {
				driver.getDevTools().addListener(event, arg -> handler.handle(driver, arg));
			});
			driver.get(url);
			long timestamp = System.currentTimeMillis();
			do {
				if (handler.isDone()) {
					break;
				} else {
					Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
				}
			} while (System.currentTimeMillis() - timestamp <= timeunit.toMillis(timeout));
			if (handler.isDone()) {

			} else {
				throw new TimeoutException();
			}
		});
	}

	private static final LazyRunnable driver_manager = LazyRunnable.wrap(() -> {
		WebDriverManager.chromedriver().setup();
//		WebDriverManager.firefoxdriver().setup();
//		WebDriverManager.edgedriver().setup();
	});

	@Override
	protected ChromeDriver create() throws Exception {
		ChromeDriver driver = new ChromeDriver();
		driver.getDevTools().createSession();
		driver.getDevTools().send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
				Optional.empty()));
		return driver;
	}

	protected void init(ChromeDriver driver) {
//		driver.getDevTools().createSession();
//		driver.getDevTools().send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
//				Optional.empty()));
		driver.get("data:,");
	}

	@Override
	protected void after(ChromeDriver driver) {
		driver.getDevTools().clearListeners();
		driver.resetCooldown();
		driver.resetInputState();
		driver.get("data:,");
//		driver.getDevTools().close();
	}

	@Override
	protected void destroy(ChromeDriver driver) {
		driver.quit();
	}

	@Override
	protected <E extends Throwable> boolean validate(ChromeDriver driver, E exception) {
		return exception == null || exception instanceof TimeoutException;
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("socksProxyHost", "localhost");
		System.setProperty("socksProxyPort", "10000");
		driver_manager.run();
		Stopwatch stopwatch = Stopwatch.createStarted();
		Map<String, Integer> map = Maps.newConcurrentMap();
		IntStream.rangeClosed(1, 10).forEach(i -> {
			try {
				String url = "https://www.flixflop.com/streams/525138018097430972/sources/204817109715256315/%E7%AC%AC"
						+ String.format("%02d", i) + "%E9%9B%86";
				AtomicBoolean done = new AtomicBoolean(false);
				DEFAULT.get().capture(url, 1, TimeUnit.MINUTES, Lists.newArrayList(Network.requestWillBeSent()),
						new CaptureHandler() {

							@Override
							public void handle(ChromeDriver driver, Object arg) {
								RequestWillBeSent req = (RequestWillBeSent) arg;
								if (req.getRequest().getUrl().endsWith(".m3u8")) {
									logger.info("{}-{}", i, req.getRequest().getUrl());
									done.set(true);
									if (Optional.ofNullable(map.putIfAbsent(req.getRequest().getUrl(), i))
											.orElse(i) == i) {

									} else {
										logger.info("{}-{}", i, "ERROR");
									}
								}
							}

							@Override
							public boolean isDone() {
								return done.get();
							}

						});
			} catch (TimeoutException e) {
				logger.error("", e);
			}
		});
		logger.info("elapsed:{}", N_humanUtils.formatMills(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
	}

}
