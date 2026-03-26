package com.kjrepo.infra.monitor.mxbean;

import org.slf4j.Logger;

import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.spi.ParamSpi;
import com.kjrepo.infra.monitor.IMonitor;

public interface IMxbeanHandler<D extends IMxbeanHolder> extends ParamSpi<D> {

	Logger logger = LoggerUtils.logger(IMonitor.class);

	void handle(D bean);

}
