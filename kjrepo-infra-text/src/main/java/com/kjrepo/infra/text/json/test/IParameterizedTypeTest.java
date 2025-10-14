package com.kjrepo.infra.text.json.test;

import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;

abstract class IParameterizedTypeTest<T> {

	Logger logger = LoggerUtils.logger();

	public void test1(T bean) {
		logger.info("{}", bean);
	}

	public abstract void test2(T bean);

}
