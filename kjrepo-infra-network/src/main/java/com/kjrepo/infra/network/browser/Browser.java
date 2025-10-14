package com.kjrepo.infra.network.browser;

import org.htmlunit.WebClient;
import com.kjrepo.infra.common.executor.PooledInfoExecutor;

public class Browser extends PooledInfoExecutor<WebClient, BrowserInfo> {

	public Browser(BrowserInfo info) {
		super(info);
	}

	@Override
	protected WebClient create() throws Exception {
		return BrowserUtils.client(info());
	}

	@Override
	protected void destroy(WebClient bean) throws Exception {
		bean.close();
	}

}
