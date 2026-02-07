package com.kjrepo.infra.runner.rpc.grpc;

import java.io.IOException;
import java.net.InetSocketAddress;
//import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.cli.CommandLine;

import com.annimon.stream.Stream;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.gregister.context.GroupRegisterFactory;
//import com.kjrepo.infra.register.spi.context.RegisterFactory;
import com.kjrepo.infra.runner.rpc.RpcAddressInfo;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcRunnerServer extends AbstractRunnerServer<GrpcRunner> {

	private final AtomicReference<ExecutorService> executor = new AtomicReference<>();

	@Override
	protected void doInit(CommandLine args) {
		executor.set(
				Executors.newFixedThreadPool(Integer.valueOf(args.getOptionValue("grpcExecutorThreadCount", "10"))));
		executor.get().execute(() -> {
			logger.info("grpc thread pool start!!!");
		});
	}

	@Override
	protected void doRun(GrpcRunner runner) {
		try {
			ServerBuilder<?> builder = ServerBuilder.forPort(0).executor(executor.get());
//			runners.forEach(runner -> {
			builder.addService(runner);
//			});
			Server server = builder.build().start();
			RpcAddressInfo address = Stream.of(server.getListenSockets()).map(socket -> (InetSocketAddress) socket)
					.map(socket -> RpcAddressInfo.address(socket.getHostName(), socket.getPort())).toList().get(0);
//			runners.forEach(runner -> {
			GroupRegisterFactory.getContext(runner.getClass()).getGroupRegister(GrpcInfo.class, RpcAddressInfo.class)
					.cadd(runner.ID(), address);
//			});
			TermHelper.addTerm("grpc", () -> {
				server.shutdown();
				server.awaitTermination();
			});
		} catch (NumberFormatException | IOException e) {
			logger.error("", e);
		}
//		return this;
	}

	@Override
	protected boolean nlock() {
		return false;
	}

}
