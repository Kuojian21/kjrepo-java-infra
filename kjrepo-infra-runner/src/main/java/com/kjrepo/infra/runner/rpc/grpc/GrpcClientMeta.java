package com.kjrepo.infra.runner.rpc.grpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
//import java.util.Map;

import org.slf4j.Logger;

//import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.cluster.ClusterInfo;
import com.kjrepo.infra.cluster.instance.InstanceInfo;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.gregister.GroupRegister;
import com.kjrepo.infra.gregister.GroupRegisterListener;
import com.kjrepo.infra.gregister.context.GroupRegisterFactory;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.RegisterEvent;
import com.kjrepo.infra.register.RegisterListener;
import com.kjrepo.infra.register.context.RegisterFactory;
import com.kjrepo.infra.runner.rpc.RpcAddressInfo;
//import com.kjrepo.infra.text.json.ConfigUtils;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClientMeta<R> {

	final Logger logger = LoggerUtils.logger(getClass());
	private final LazySupplier<Cluster<R>> cluster;
	private final Class<R> clazz;
	private final GroupRegister<Object, RpcAddressInfo> gregister;
	private final Register<GrpcInfo> register;

	@SuppressWarnings({ "unchecked" })
	public GrpcClientMeta(String key, Method method) {
		this.clazz = (Class<R>) method.getReturnType().getInterfaces()[0];
		this.gregister = GroupRegisterFactory.getContext(this.clazz).getGroupRegister(Object.class,
				RpcAddressInfo.class);
		this.register = RegisterFactory.getContext(this.clazz).getRegister(GrpcInfo.class);

		this.gregister.caddListener(key, new GroupRegisterListener() {

			@Override
			public void onCreate(String ckey) {
				cluster().get().add(ckey);
			}

			@Override
			public void onRemove(String ckey) {
				cluster().get().remove(ckey);
			}

		});
		this.gregister.caddListener(key, new RegisterListener<RpcAddressInfo>() {
			@Override
			public void onChange(RegisterEvent<RpcAddressInfo> event) {
				String key = event.getKey();
				cluster().get().refresh(key.substring(key.lastIndexOf("/") + 1));
			}

		});
		this.cluster = LazySupplier.wrap(() -> new Cluster<R>(clazz, () -> {
			ClusterInfo<RpcAddressInfo> info = new ClusterInfo<RpcAddressInfo>();
			info.setSelector(register.get(key).getSelector());
			info.setInstanceInfos(
					Stream.of(gregister.cget(key)).map(p -> InstanceInfo.of(p.getKey(), p.getValue())).toList());
			return info;
		}, rinfo -> {
			try {
				ManagedChannel channel = ManagedChannelBuilder
						.forAddress(((RpcAddressInfo) rinfo).getHost(), ((RpcAddressInfo) rinfo).getPort())
						.usePlaintext().build();
				return (R) method.invoke(null, new Object[] { channel });
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}, res -> {
			try {
				ManagedChannel channel = (ManagedChannel) res.getClass()
						.getDeclaredMethod("getChannel", new Class<?>[] {}).invoke(res, new Object[] {});
				channel.shutdown();
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				logger.error("", e);
			}
		}));
	}

	public LazySupplier<Cluster<R>> cluster() {
		return cluster;
	}

	public Class<R> clazz() {
		return this.clazz;
	}

}
