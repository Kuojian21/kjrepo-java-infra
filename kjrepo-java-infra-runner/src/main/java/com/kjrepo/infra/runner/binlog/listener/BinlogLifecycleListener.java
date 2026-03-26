package com.kjrepo.infra.runner.binlog.listener;

import org.slf4j.Logger;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogClient.LifecycleListener;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.runner.binlog.holder.BinlogRunnerHolder;

public class BinlogLifecycleListener implements LifecycleListener {

	private final Logger logger = LoggerUtils.logger(getClass());
	private final BinlogRunnerHolder holder;

	public BinlogLifecycleListener(BinlogRunnerHolder holder) {
		this.holder = holder;
	}

	@Override
	public void onConnect(BinaryLogClient client) {
		holder.client(client);
		holder.status(client.getBinlogFilename(), client.getBinlogPosition(), client.getGtidSet());
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
