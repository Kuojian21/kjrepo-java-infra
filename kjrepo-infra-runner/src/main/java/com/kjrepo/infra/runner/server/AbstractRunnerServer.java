package com.kjrepo.infra.runner.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.distrib.lock.DLock;
import com.kjrepo.infra.distrib.lock.context.DLockFactory;
import com.kjrepo.infra.runner.Runner;
import com.kjrepo.infra.server.args.ServerArgs;

public abstract class AbstractRunnerServer<R extends Runner> implements RunnerServer<R> {

	protected final Logger logger = LoggerUtils.logger(this.getClass());

	protected final LazySupplier<CommandLine> commandLine = LazySupplier
			.wrap(() -> ServerArgs.args().commandLine(this.args_prefix(), this.args_options()));

	protected AbstractRunnerServer() {
		super();
		this.init();
	}

	@Override
	public final RunnerServer<R> run(R runner) {
		if (nlock()) {
			Class<?> clazz = this.getClass();
			while (clazz.getSuperclass() != AbstractRunnerServer.class) {
				clazz = clazz.getSuperclass();
			}
			DLock lock = DLockFactory.getContext(getClass())
					.getLock("dlock/runner/" + clazz.getSimpleName().replace("RunnerServer", "").toLowerCase() + "/"
							+ (StringUtils.isEmpty(runner.ID())
									? runner.getClass().getSimpleName().replaceAll("\\$", "_")
									: runner.ID()));
			lock.lock();
			TermHelper.addTerm(runner.module(), () -> lock.unlock());
		}

		this.doRun(runner);
		return this;
	}

	protected void init() {

	}

	protected String args_prefix() {
		String prefix = this.getClass().getName().replace("." + this.getClass().getSimpleName(), "");
		return prefix.substring(prefix.lastIndexOf(".") + 1);
	}

	protected Options args_options() {
		return new Options();
	}

	protected abstract void doRun(R runner);

	protected abstract boolean nlock();

}
