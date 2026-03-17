package com.kjrepo.infra.monitor;

import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;

public interface IMonitor {

	Logger logger = LoggerUtils.logger(IMonitor.class);

	void monitor();

}
