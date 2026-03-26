package com.kjrepo.infra.runner.binlog.server;

import java.io.IOException;
import java.util.concurrent.ThreadFactory;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.common.utils.RunUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.runner.binlog.BinlogRunner;
import com.kjrepo.infra.runner.binlog.holder.BinlogRunnerHolder;
import com.kjrepo.infra.runner.binlog.info.BinlogLoginInfo;
import com.kjrepo.infra.runner.binlog.info.BinlogStatusInfo;
import com.kjrepo.infra.runner.binlog.listener.BinlogEventListener;
import com.kjrepo.infra.runner.binlog.listener.BinlogLifecycleListener;
import com.kjrepo.infra.runner.common.RunnerConstants;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;

public class BinlogRunnerServer extends AbstractRunnerServer<BinlogRunner> {

	private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("binlog-connect-%d")
			.setDaemon(false).build();

	@Override
	protected void doRun(BinlogRunner runner) throws IllegalStateException, IOException {
		BinlogRunnerHolder holder = BinlogRunnerHolder.of(runner);
		String path = RunnerConstants.register_binlog + runner.ID() + "/login";
		Register<BinlogLoginInfo> register = holder.context().getRegister(BinlogLoginInfo.class);

		LazySupplier<BinaryLogClient> client_supplier = LazySupplier.wrap(() -> {
			BinlogLoginInfo loginInfo = register.get(path);
			BinaryLogClient client = new BinaryLogClient(loginInfo.getHostname(), loginInfo.getPort(),
					loginInfo.getSchema(), loginInfo.getUsername(), loginInfo.getPassword());
			client.registerLifecycleListener(new BinlogLifecycleListener(holder));
			client.registerEventListener(new BinlogEventListener(holder));

			BinlogStatusInfo statusInfo = holder.status();
			if (statusInfo != null) {
				client.setBinlogFilename(statusInfo.getBinlogFilename());
				client.setBinlogPosition(statusInfo.getBinlogPosition());
				client.setGtidSet(statusInfo.getGtidSet());
			}
			THREAD_FACTORY.newThread(() -> RunUtils.catching(() -> client.connect())).start();
			return client;
		});
		register.addListener(path, event -> {
			client_supplier.refresh(client -> client.disconnect());
			client_supplier.get();
		});
		TermHelper.addTerm(runner.module(), holder::close);
		client_supplier.get();
	}

	@Override
	protected boolean nlock() {
		return true;
	}

}
