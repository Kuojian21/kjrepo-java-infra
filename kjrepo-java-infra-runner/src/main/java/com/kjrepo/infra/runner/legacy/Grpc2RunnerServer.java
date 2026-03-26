package com.kjrepo.infra.runner.legacy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import com.annimon.stream.Stream;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.register.group.context.GroupRegisterFactory;
import com.kjrepo.infra.runner.rpc.grpc.info.GrpcInfo;
import com.kjrepo.infra.runner.rpc.grpc.info.GrpcItemInfo;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class Grpc2RunnerServer extends AbstractRunnerServer<Grpc2Runner> {

	private final AtomicReference<ExecutorService> executor = new AtomicReference<>();

	@Override
	protected void init() {
		executor.set(Executors.newFixedThreadPool(
				Integer.valueOf(super.commandLine.get().getOptionValue("grpcExecutorThreadCount", "10"))));
		executor.get().execute(() -> {
			logger.info("grpc thread pool start!!!");
		});
	}

	@Override
	protected void doRun(Grpc2Runner runner) {
		try {
			ServerBuilder<?> builder = ServerBuilder.forPort(0).executor(executor.get());
			builder.addService(runner);
			Server server = builder.build().start();
			GrpcItemInfo address = Stream.of(server.getListenSockets()).map(socket -> (InetSocketAddress) socket)
					.map(socket -> GrpcItemInfo.address(socket.getHostName(), socket.getPort())).toList().get(0);
			GroupRegisterFactory.getContext(runner.getClass()).getGroupRegister(GrpcInfo.class, GrpcItemInfo.class)
					.cadd(runner.ID(), address);
			TermHelper.addTerm("grpc", () -> {
				server.shutdown();
				server.awaitTermination();
			});
		} catch (NumberFormatException | IOException e) {
			logger.error("", e);
		}
	}

	@Override
	protected boolean nlock() {
		return false;
	}

}
