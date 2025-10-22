package com.kjrepo.infra.runner.binlog;

import org.slf4j.Logger;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogClient.LifecycleListener;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;

public class BinlogLifecycleListener implements LifecycleListener {

	private final Logger logger = LoggerUtils.logger(getClass());
	private final BinlogRunner runner;
	private final Register<BinlogStatusInfo> register;

	public BinlogLifecycleListener(BinlogRunner runner) {
		this.runner = runner;
		this.register = RegisterFactory.getContext(runner.getClass()).getRegister(BinlogStatusInfo.class);
	}

	@Override
	public void onConnect(BinaryLogClient client) {
		TermHelper.addTerm(runner.module(), () -> client.disconnect());
		BinlogStatusInfo info = register.get(runner.ID());
		if (info == null) {
			info = new BinlogStatusInfo();
			info.setBinlogFilename(client.getBinlogFilename());
			info.setBinlogPosition(client.getBinlogPosition());
			info.setGtidSet(client.getGtidSet());
			this.register.set(runner.ID(), info);
		}
	}

	@Override
	public void onCommunicationFailure(BinaryLogClient client, Exception ex) {
		logger.error("onCommunicationFailure", ex);
	}

	@Override
	public void onEventDeserializationFailure(BinaryLogClient client, Exception ex) {
		logger.error("onEventDeserializationFailure", ex);
	}

	@Override
	public void onDisconnect(BinaryLogClient client) {
		logger.info("onDisconnect");
	}

}
