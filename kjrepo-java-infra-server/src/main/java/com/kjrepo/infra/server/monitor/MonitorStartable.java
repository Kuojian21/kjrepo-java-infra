package com.kjrepo.infra.server.monitor;

import com.kjrepo.infra.monitor.startup.Monitor;
import com.kjrepo.infra.server.startup.Startable;

public class MonitorStartable implements Startable {

	@Override
	public void startup() throws Exception {
		Monitor.start();
	}

}
