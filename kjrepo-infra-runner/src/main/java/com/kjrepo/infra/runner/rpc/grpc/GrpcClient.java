package com.kjrepo.infra.runner.rpc.grpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

	private final GrpcClientMeta meta;
	private final Map<Class<?>, Method> methods;

	public GrpcClient(String key, Class<?> clazz) {
		this.meta = new GrpcClientMeta(key, clazz);
		this.methods = Stream.of("newBlockingStub", "newFutureStub", "newStub").map(m -> {
			try {
				return clazz.getDeclaredMethod(m, new Class<?>[] { Channel.class });
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toMap(m -> m.getReturnType().getInterfaces()[0], m -> m));
	}

	@SuppressWarnings("unchecked")
	public <R> R get(Class<R> clazz) {
		try {
			return (R) methods.get(clazz).invoke(null, new Object[] { meta.channel() });
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
