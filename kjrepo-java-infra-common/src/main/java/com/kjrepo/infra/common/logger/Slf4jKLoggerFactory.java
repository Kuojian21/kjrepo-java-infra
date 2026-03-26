package com.kjrepo.infra.common.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jKLoggerFactory implements IKLoggerFactory {

	@Override
	public Logger getLogger(String name) {
		return LoggerFactory.getLogger(name);
	}

}
