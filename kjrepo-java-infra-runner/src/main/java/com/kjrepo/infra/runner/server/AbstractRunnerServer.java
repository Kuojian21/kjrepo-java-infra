package com.kjrepo.infra.runner.server;

import java.util.concurrent.ThreadFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kjrepo.infra.common.lazy.LazyRunnable;
//import com.kjrepo.infra.common.hook.HookHelper;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.utils.RunUtils;
import com.kjrepo.infra.dlock.DLock;
import com.kjrepo.infra.dlock.context.DLockFactory;
import com.kjrepo.infra.runner.Runner;
import com.kjrepo.infra.runner.common.RunnerConstants;
import com.kjrepo.infra.server.args.ServerArgs;

public abstract class AbstractRunnerServer<R extends Runner> implements RunnerServer<R> {

	private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("runner-dlock-%d")
			.setDaemon(false).build();

	protected final Logger logger = LoggerUtils.logger(this.getClass());
	protected final LazySupplier<CommandLine> commandLine = LazySupplier
			.wrap(() -> ServerArgs.args().commandLine(this.args_prefix(), this.args_options()));
	private final LazyRunnable init = LazyRunnable.wrap(AbstractRunnerServer.this::init);

	@Override
	public final void run(R runner) {
		init.run();
		THREAD_FACTORY.newThread(() -> {
			if (nlock() && StringUtils.isNotEmpty(runner.ID())) {
				DLock lock = DLockFactory.getContext(runner.getClass())
						.getLock(RunnerConstants.dlock + runner.module() + "/" + runner.ID());
				lock.lock();
			}
			RunUtils.throwing(() -> this.doRun(runner));
		}).start();
	}

	protected void init() {

	}

	protected String args_prefix() {
		String prefix = this.getClass().getName().replace(".server." + this.getClass().getSimpleName(), "");
		return prefix.substring(prefix.lastIndexOf(".") + 1);
	}

	protected Options args_options() {
		return new Options();
	}

	protected abstract void doRun(R runner) throws Exception;

	protected abstract boolean nlock();

}
