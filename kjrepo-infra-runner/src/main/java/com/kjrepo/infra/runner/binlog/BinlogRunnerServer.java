package com.kjrepo.infra.runner.binlog;

import java.io.IOException;
import java.util.List;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.kjrepo.infra.register.context.RegisterFactory;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;

public class BinlogRunnerServer extends AbstractRunnerServer<BinlogRunner> {

	@Override
	public BinlogRunnerServer run(List<BinlogRunner> runners) {
		runners.forEach(runner -> {
			try {
				ClientLoginInfo bean = RegisterFactory.getContext(runner.getClass()).getRegister(ClientLoginInfo.class)
						.get(runner.ID() + "/login");
				BinaryLogClient client = new BinaryLogClient(bean.getHostname(), bean.getPort(), bean.getSchema(),
						bean.getUsername(), bean.getPassword());
				client.registerLifecycleListener(new BinlogLifecycleListener(runner));
				client.registerEventListener(new BinlogEventListener(runner));
//				if (BinlogRunnerServer.this.lock().lock(runner.jobID())) {
				ClientStatusInfo statusInfo = RegisterFactory.getContext(runner.getClass())
						.getRegister(ClientStatusInfo.class).get(runner.ID() + "/status");
				if (statusInfo != null) {
					client.setBinlogFilename(statusInfo.getBinlogFilename());
					client.setBinlogPosition(statusInfo.getBinlogPosition());
					client.setGtidSet(statusInfo.getGtidSet());
				}
				client.connect();
//				}
			} catch (IllegalStateException | IOException e) {
				logger.error("", e);
			}
		});
		return this;
	}

}
