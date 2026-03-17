package com.kjrepo.infra.server.startup;

import java.util.ServiceLoader;

import org.slf4j.Logger;

import com.annimon.stream.Stream;
import com.kjrepo.infra.common.args.Args;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.trace.TraceIDUtils;
import com.kjrepo.infra.server.args.ServerArgs;

public class StartableMain {

	private static final Logger logger = LoggerUtils.logger(StartableMain.class);

	public static void main(String[] args) throws Exception {
		try {
			TraceIDUtils.generate();
			ServerArgs.args(Args.of(args));
			for (Startable startable : Stream.of(ServiceLoader.load(Startable.class)).sorted().toList()) {
				logger.info("The startable:{} will start!!!", startable.getClass().getSimpleName());
				startable.startup();
				logger.info("The startable:{} have started success!!!", startable.getClass().getSimpleName());
			}
		} catch (Exception e) {
			logger.error("StartablaMain exception!!!", e);
			throw e;
		} finally {
			TraceIDUtils.clear();
		}
	}
}
