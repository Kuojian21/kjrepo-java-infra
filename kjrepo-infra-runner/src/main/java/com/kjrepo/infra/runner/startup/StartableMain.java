package com.kjrepo.infra.runner.startup;

import java.util.ServiceLoader;

import org.slf4j.Logger;

import com.annimon.stream.Stream;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.runner.server.RunnerServerFactory;
import com.kjrepo.infra.runner.server.args.Args;
import com.kjrepo.infra.trace.utils.TraceIDUtils;

public class StartableMain {

	private static final Logger logger = LoggerUtils.logger(StartableMain.class);

	public static void main(String[] args) {
		TraceIDUtils.generate();
		try {
			RunnerServerFactory.args(Args.of(args));
			Stream.of(ServiceLoader.load(Startable.class)).sorted().forEach(startable -> {
				try {
					logger.info("startable:{} is starting!!!", startable.getClass().getSimpleName());
					startable.startup();
					logger.info("startable:{} started success!!!", startable.getClass().getSimpleName());
				} catch (Throwable e) {
					logger.error("startable:" + startable.getClass().getSimpleName() + " started exception!!!", e);
				}
			});
		} finally {
			TraceIDUtils.clear();
		}
	}

}
