package com.kjrepo.infra.runner.binlog;

import java.util.Map;

import com.kjrepo.infra.runner.Runner;

public abstract class BinlogRunner implements Runner {

	private final Map<String, BinlogResolver<?>> resolvers;

	public BinlogRunner(Map<String, BinlogResolver<?>> resolvers) {
		super();
		this.resolvers = resolvers;
	}

	public Map<String, BinlogResolver<?>> resolvers() {
		return this.resolvers;
	}

}
