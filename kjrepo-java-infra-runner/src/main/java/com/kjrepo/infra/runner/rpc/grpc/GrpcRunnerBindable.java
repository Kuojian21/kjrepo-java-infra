package com.kjrepo.infra.runner.rpc.grpc;

import java.util.List;

import com.google.common.collect.Lists;

import io.grpc.BindableService;

public interface GrpcRunnerBindable extends GrpcRunner, BindableService {

	@Override
	default List<BindableService> services() {
		return Lists.newArrayList(this);
	}

}
