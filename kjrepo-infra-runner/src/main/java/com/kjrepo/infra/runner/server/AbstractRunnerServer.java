package com.kjrepo.infra.runner.server;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;

import com.kjrepo.infra.common.lazy.LazyRunnable;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.runner.Runner;

public abstract class AbstractRunnerServer<R extends Runner> implements RunnerServer<R> {

	protected final Logger logger = LoggerUtils.logger(this.getClass());

	private final AtomicReference<LazySupplier<CommandLine>> commandLine = new AtomicReference<>(
			LazySupplier.wrap(() -> CommandLine.builder().build()));

	private final LazyRunnable initializer = LazyRunnable.wrap(() -> {
		logger.info("The server [{}] is initializing!!!", AbstractRunnerServer.this.getClass().getSimpleName());
		this.doInit(commandLine.get().get());
		logger.info("The server [{}]'s initialization has done!!!",
				AbstractRunnerServer.this.getClass().getSimpleName());
	});

	@Override
	public final RunnerServer<R> init() {
		this.initializer.run();
		return this;
	}

	protected void doInit(CommandLine args) {
		logger.info("{} be inited!!!", this.getClass().getSimpleName());
	}

	@Override
	public final void setCommandLine(LazySupplier<CommandLine> args) {
		this.commandLine.set(args);
	}

}
