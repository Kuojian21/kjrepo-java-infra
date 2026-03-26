package com.kjrepo.infra.runner.server;

import java.util.List;

import org.slf4j.Logger;

import com.annimon.stream.Stream;
import com.kjrepo.infra.common.args.Args;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.spring.SpringBeanFactory;
import com.kjrepo.infra.runner.Runner;
import com.kjrepo.infra.runner.RunnerGroup;
import com.kjrepo.infra.server.args.ServerArgs;

public class RunnerServerMain {

	private static final Logger logger = LoggerUtils.logger(RunnerServerMain.class);

	public static <R extends Runner> void main(String[] args) {
		try {
			ServerArgs.args(Args.of(args));
			new RunnerGroup() {
				@Override
				public List<? extends Runner> jobList() {
					return Stream.of(ServerArgs.args().option("bean").get()).flatMap(bn -> Stream.of(bn.split(",")))
							.map(bn -> SpringBeanFactory.getBean(bn, Runner.class)).toList();
				}
			}.execute();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
