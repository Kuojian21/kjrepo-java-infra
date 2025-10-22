package com.kjrepo.infra.network.browser;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.slf4j.Logger;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.executor.PooledInfoDefault;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class BrowserInfo extends PooledInfoDefault<WebClient> {

	private static Logger logger = LoggerUtils.logger(BrowserInfo.class);
	private static Map<String, BrowserVersion> browsers = Stream.of(BrowserVersion.ALL_SUPPORTED_BROWSERS)
			.collect(Collectors.toMap(br -> br.getNickname().toLowerCase(), br -> br));
	static {
		logger.info("All supported browsers: {}", StringUtils.join(browsers.keySet()));
	}

	private String browserVersion = "CHROME";
	private Map<String, Object> options = Maps.newHashMap();
	private GenericObjectPoolConfig<WebClient> poolConfig;
	private Map<String, Object> listeners = Maps.newHashMap();

	public String getBrowserVersion() {
		return browserVersion;
	}

	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}

	public Map<String, Object> getOptions() {
		return options;
	}

	public void setOptions(Map<String, Object> options) {
		this.options = options;
	}

	public GenericObjectPoolConfig<WebClient> getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(GenericObjectPoolConfig<WebClient> poolConfig) {
		this.poolConfig = poolConfig;
	}

	public Map<String, Object> getListeners() {
		return listeners;
	}

	public void setListeners(Map<String, Object> listeners) {
		this.listeners = listeners;
	}

	public BrowserVersion toBrowserVersion() {
		return browsers.getOrDefault(browserVersion.toLowerCase(), BrowserVersion.CHROME);
	}

}
