package com.kjrepo.infra.runner.binlog;

import java.util.List;

public abstract class BinlogResolver<T> {

	private final BinlogMapper<T> mapper;

	public BinlogResolver(BinlogMapper<T> mapper) {
		this.mapper = mapper;
	}

	public abstract void insert(List<?> data);

	public abstract void update(List<?> oData, List<?> nData);

	public abstract void delete(List<?> data);

	public BinlogMapper<T> mapper() {
		return this.mapper;
	}

}
