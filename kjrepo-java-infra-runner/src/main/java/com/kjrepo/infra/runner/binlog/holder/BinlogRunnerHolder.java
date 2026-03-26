package com.kjrepo.infra.runner.binlog.holder;

import java.io.IOException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;

import com.annimon.stream.function.Supplier;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.runner.binlog.BinlogRunner;
import com.kjrepo.infra.runner.binlog.info.BinlogStatusInfo;
import com.kjrepo.infra.runner.common.RunnerConstants;
import com.kjrepo.infra.runner.common.RunnerHolder;

public class BinlogRunnerHolder extends RunnerHolder<BinlogRunner> {

	public static BinlogRunnerHolder of(BinlogRunner runner) {
		return new BinlogRunnerHolder(runner);
	}

	private static final Logger logger = LoggerUtils.logger(BinlogRunnerHolder.class);
	private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("binlog-sync-%d")
			.setDaemon(false).build();

	private final AtomicReference<BinlogStatusInfo> status = new AtomicReference<>(new BinlogStatusInfo());
	private final Thread thread;
	private final AtomicBoolean inited = new AtomicBoolean(false);
	private volatile BinaryLogClient client;

	private BinlogRunnerHolder(BinlogRunner runner) {
		super(runner);
		this.thread = THREAD_FACTORY.newThread(() -> {
			while (!Thread.interrupted()) {
				try {
					Thread.sleep(TimeUnit.MINUTES.toMillis(1));
					if (inited.get()) {
						context().getRegister(BinlogStatusInfo.class).set(
								RunnerConstants.register_binlog + runner.ID() + "/status",
								new Supplier<BinlogStatusInfo>() {

									@Override
									public BinlogStatusInfo get() {
										synchronized (status) {
											return status.get();
										}
									}

								}.get());
					}
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		});
		this.thread.start();
	}

	public void client(BinaryLogClient client) {
		this.client = client;
	}

	public BinlogStatusInfo status() {
		return context().getRegister(BinlogStatusInfo.class)
				.get(RunnerConstants.register_binlog + runner().ID() + "/status");
	}

	public void status(String binlogFilename, long binlogPosition, String gtidSet) {
		synchronized (status) {
			status.get().setBinlogFilename(binlogFilename);
			status.get().setBinlogPosition(binlogPosition);
			status.get().setGtidSet(gtidSet);
			inited.set(true);
		}
	}

	public void status(String binlogFilename, long binlogPosition) {
		synchronized (status) {
			status.get().setBinlogFilename(binlogFilename);
			status.get().setBinlogPosition(binlogPosition);
			inited.set(true);
		}
	}

	public void status(long binlogPosition) {
		synchronized (status) {
			status.get().setBinlogPosition(binlogPosition);
			inited.set(true);
		}
	}

	public void status(String gtidSet) {
		synchronized (status) {
			status.get().setGtidSet(gtidSet);
			inited.set(true);
		}
	}

	public void close() throws InterruptedException, IOException {
		if (this.client != null) {
			this.client.disconnect();
		}
		this.thread.interrupt();
		this.thread.join();
	}

}
