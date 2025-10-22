package com.kjrepo.infra.runner.binlog;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.kjrepo.infra.distrib.lock.DLock;
import com.kjrepo.infra.distrib.lock.context.DLockFactory;
import com.kjrepo.infra.register.context.RegisterFactory;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;

public class BinlogRunnerServer extends AbstractRunnerServer<BinlogRunner> {

	@Override
	public BinlogRunnerServer run(List<BinlogRunner> runners) {
		runners.forEach(runner -> {
			new Thread(() -> {
				DLock lock = DLockFactory.getContext(getClass())
						.getLock(StringUtils.isEmpty(runner.ID()) ? null : "/binlog/" + runner.ID() + "/lock");
				lock.lock();
				try {
					BinlogLoginInfo bean = RegisterFactory.getContext(runner.getClass())
							.getRegister(BinlogLoginInfo.class).get("/binlog/" + runner.ID() + "/login");
					BinaryLogClient client = new BinaryLogClient(bean.getHostname(), bean.getPort(), bean.getSchema(),
							bean.getUsername(), bean.getPassword());
					client.registerLifecycleListener(new BinlogLifecycleListener(runner));
					client.registerEventListener(new BinlogEventListener(runner));
					BinlogStatusInfo statusInfo = RegisterFactory.getContext(runner.getClass())
							.getRegister(BinlogStatusInfo.class).get("/binlog/" + runner.ID() + "/status");
					if (statusInfo != null) {
						client.setBinlogFilename(statusInfo.getBinlogFilename());
						client.setBinlogPosition(statusInfo.getBinlogPosition());
						client.setGtidSet(statusInfo.getGtidSet());
					}
					client.connect();
				} catch (IllegalStateException | IOException e) {
					logger.error("", e);
				}
			}).start();
		});
		return this;
	}

}
