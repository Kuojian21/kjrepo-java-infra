package com.kjrepo.infra.runner.rpc.grpc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.cli.Options;

import com.annimon.stream.Stream;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.register.group.context.GroupRegisterFactory;
import com.kjrepo.infra.runner.server.AbstractRunnerServer;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcRunnerServer extends AbstractRunnerServer<GrpcRunner> {

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
	protected void doRun(GrpcRunner runner) {
		try {
			ServerBuilder<?> builder = ServerBuilder.forPort(0).executor(executor.get());
			runner.services().forEach(builder::addService);
			Server server = builder.build().start();
			GrpcInfoItem address = Stream.of(server.getListenSockets()).map(socket -> (InetSocketAddress) socket)
					.map(socket -> GrpcInfoItem.address(socket.getHostName(), socket.getPort())).toList().get(0);
			GroupRegisterFactory.getContext(runner.getClass()).getGroupRegister(GrpcInfo.class, GrpcInfoItem.class)
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

	public Options args_options() {
		Options options = new Options();
		options.addOption("", "grpcExecutorThreadCount", true, "grpcExecutorThreadCount");
		return options;
	}

}
