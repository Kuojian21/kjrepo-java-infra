package com.kjrepo.infra.reporter;

import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.reporter.bean.IReporterBean;

public interface IReporter<D extends IReporterBean> {

	Logger logger = LoggerUtils.logger();

	void report(D bean);

}
