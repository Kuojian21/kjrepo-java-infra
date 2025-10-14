package com.kjrepo.infra.runner.utils;

import java.util.List;

import com.google.common.collect.Lists;
import com.kjrepo.infra.runner.Runner;
import com.kjrepo.infra.runner.server.RunnerServer;

public class RunnerUtils {

	public static <R extends Runner> void run(RunnerServer<? extends Runner> server, R runner) {
		run(server, Lists.newArrayList(runner));
	}

	@SuppressWarnings("unchecked")
	public static <R extends Runner> void run(RunnerServer<? extends Runner> server, List<? extends Runner> runners) {
		((RunnerServer<R>) server).init().run((List<R>) runners);
	}

}
