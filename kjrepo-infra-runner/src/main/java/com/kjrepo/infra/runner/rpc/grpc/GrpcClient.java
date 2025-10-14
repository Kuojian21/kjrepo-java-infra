package com.kjrepo.infra.runner.rpc.grpc;

import java.util.Map;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.collect.Maps;
//import com.kjrepo.infra.register.Register;
//import com.kjrepo.infra.register.group.GroupRegister;
//import com.kjrepo.infra.register.spi.context.GroupRegisterFactory;
//import com.kjrepo.infra.register.spi.context.RegisterFactory;
//import com.kjrepo.infra.runner.rpc.RpcAddressInfo;

import io.grpc.Channel;

public class GrpcClient {

	private static final Map<String, GrpcClient> repo = Maps.newConcurrentMap();

	public static <R> R client(String key, Class<R> clazz) {
		try {
			Class<?> eclazz = Class.forName(clazz.getName().substring(0, clazz.getName().indexOf("$")));
			return repo.computeIfAbsent(key, k -> new GrpcClient(key, eclazz)).get(clazz);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private final Map<Class<?>, GrpcClientMeta<?>> clusters;

	@SuppressWarnings("unchecked")
	public GrpcClient(String key, Class<?> clazz) {
		try {
			this.clusters = Stream.of(
					new GrpcClientMeta<>(key,
							clazz.getDeclaredMethod("newBlockingStub", new Class<?>[] { Channel.class })),
					new GrpcClientMeta<>(key,
							clazz.getDeclaredMethod("newFutureStub", new Class<?>[] { Channel.class })),
					new GrpcClientMeta<>(key, clazz.getDeclaredMethod("newStub", new Class<?>[] { Channel.class })))
					.collect(Collectors.toMap(m -> m.clazz(), m -> m));
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <R> R get(Class<R> clazz) {
		return (R) clusters.get(clazz).cluster().get().getResource();
	}

}
