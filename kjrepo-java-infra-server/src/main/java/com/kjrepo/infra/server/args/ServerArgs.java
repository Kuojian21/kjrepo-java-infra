package com.kjrepo.infra.server.args;

import com.kjrepo.infra.common.args.Args;

public class ServerArgs {

	private static Args args;

	public synchronized static void args(Args args) {
		if (ServerArgs.args == null) {
			ServerArgs.args = args;
		} else {
			throw new RuntimeException("The args has already been setted!!!");
		}
	}

	public synchronized static Args args() {
		if (ServerArgs.args == null) {
			throw new NullPointerException();
		} else {
			return ServerArgs.args;
		}
	}

}
