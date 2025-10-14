package com.kjrepo.infra.runner.rpc.grpc;

import com.kjrepo.infra.runner.Runner;

import io.grpc.BindableService;

public interface GrpcRunner extends Runner, BindableService {

}
