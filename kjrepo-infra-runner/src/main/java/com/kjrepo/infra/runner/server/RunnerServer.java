package com.kjrepo.infra.runner.server;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import com.google.common.collect.Lists;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.runner.Runner;

public interface RunnerServer<R extends Runner> {

	RunnerServer<R> run(List<R> runners);

	RunnerServer<R> init();

	void setCommandLine(LazySupplier<CommandLine> cl);

	default String aprefix() {
		String prefix = this.getClass().getName().replace("." + this.getClass().getSimpleName(), "");
		return prefix.substring(prefix.lastIndexOf(".") + 1);
	}

	default Options options(Options options) {
		return options;
	}

	default RunnerServer<R> run(R runner) {
		return this.run(Lists.newArrayList(runner));
	}

}
