package com.kjrepo.infra.runner.rpc.grpc.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import io.grpc.MethodDescriptor;

import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.cluster.impl.ClusterFactory;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.number.N_humanUtils;
import com.kjrepo.infra.common.utils.ProxyUtils;
import com.kjrepo.infra.perf.utils.PerfUtils;
import com.kjrepo.infra.register.group.context.GroupRegisterFactory;
import com.kjrepo.infra.runner.common.RunnerConstants;
import com.kjrepo.infra.runner.rpc.grpc.info.GrpcInfo;
import com.kjrepo.infra.runner.rpc.grpc.info.GrpcItemInfo;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@SuppressWarnings("unchecked")
public class GrpcClient {

	private static final Map<String, GrpcClient> repo = Maps.newConcurrentMap();

	public static <R> R client(String key, Class<R> clazz) {
		try {
			Class<?> eclazz = Class.forName(clazz.getName().substring(0, clazz.getName().indexOf("$")));
			return repo.computeIfAbsent(key, k -> new GrpcClient(key, eclazz)).get(clazz);
		} catch (ClassNotFoundException e) {
			logger.error("", e);
			throw new RuntimeException(e);
		}
	}

	private static final Logger logger = LoggerUtils.logger(GrpcClient.class);
	private final Cluster<ManagedChannel> cluster;
	private final Map<Class<?>, Method> methods;

	public GrpcClient(String key, Class<?> clazz) {
		this.cluster = ClusterFactory.gcluster(
				GroupRegisterFactory.getContext().getGroupRegister(GrpcInfo.class, GrpcItemInfo.class),
				RunnerConstants.register_grpc + key,
				info -> ManagedChannelBuilder.forAddress(((InstanceInfo<GrpcItemInfo>) info).getInfo().getHost(),
						((InstanceInfo<GrpcItemInfo>) info).getInfo().getPort()).usePlaintext().build(),
				ManagedChannel::shutdown);
		this.methods = Stream.of("newBlockingStub", "newFutureStub", "newStub").map(m -> {
			try {
				return clazz.getDeclaredMethod(m, new Class<?>[] { Channel.class });
			} catch (NoSuchMethodException | SecurityException e) {
				logger.error("", e);
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toMap(m -> m.getReturnType(), m -> m));

	}

	public <R> R get(Class<R> clazz) {
		try {
			ManagedChannel o_channel = this.cluster.getResource();
			ManagedChannel n_channel = ProxyUtils.cglib(ManagedChannel.class, (obj, method, args, proxy) -> {
				if (method.getName().equals("newCall") && args.length > 0 && args[0] instanceof MethodDescriptor) {
					Stopwatch stopwatch = Stopwatch.createStarted();
					MethodDescriptor<?, ?> descriptor = (MethodDescriptor<?, ?>) args[0];
					try {
						Object rtn = method.invoke(o_channel, args);
						PerfUtils.perf(PerfUtils.N_client_grpc, "exec", descriptor.getFullMethodName()).count(1)
								.micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
						logger.debug(descriptor.getFullMethodName() + " elapsed:"
								+ N_humanUtils.formatMicros(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
						return rtn;
					} catch (Exception e) {
						PerfUtils
								.perf(PerfUtils.N_client_grpc, e.getClass().getSimpleName(),
										descriptor.getFullMethodName())
								.count(1).micro(stopwatch.elapsed(TimeUnit.MICROSECONDS)).logstash();
						logger.error(descriptor.getFullMethodName() + " elapsed:"
								+ N_humanUtils.formatMicros(stopwatch.elapsed(TimeUnit.MICROSECONDS)), e);
						throw e;
					}
				} else {
					return method.invoke(o_channel, args);
				}
			});
			return (R) methods.get(clazz).invoke(null, new Object[] { n_channel });
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
