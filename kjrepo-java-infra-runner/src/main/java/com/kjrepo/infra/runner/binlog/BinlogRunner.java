package com.kjrepo.infra.runner.binlog;

import java.util.Map;

import com.kjrepo.infra.runner.Runner;

public abstract class BinlogRunner implements Runner {

	private final Map<String, BinlogResolver<?>> tableResolverMap;

	public BinlogRunner(Map<String, BinlogResolver<?>> tableResolverMap) {
		this.tableResolverMap = tableResolverMap;
	}

	public Map<String, BinlogResolver<?>> tableResolverMap() {
		return this.tableResolverMap;
	}

}
