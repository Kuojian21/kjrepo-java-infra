package com.kjrepo.infra.runner.server;

import java.util.List;

import org.slf4j.Logger;

import com.annimon.stream.Stream;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.spring.SpringBeanFactory;
import com.kjrepo.infra.runner.Runner;
import com.kjrepo.infra.runner.RunnerGroup;
import com.kjrepo.infra.runner.server.args.Args;

public class RunnerServerMain {

	private static final Logger logger = LoggerUtils.logger();

	public static <R extends Runner> void main(String[] args) {
		try {
			Args bargs = Args.of(args);
			RunnerServerFactory.args(bargs);
			new RunnerGroup() {
				@Override
				public List<? extends Runner> jobList() {
					return Stream.of(bargs.arg("bean")).flatMap(bn -> Stream.of(bn.split(",")))
							.map(bn -> SpringBeanFactory.getBean(bn, Runner.class)).toList();
				}
			}.execute();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
