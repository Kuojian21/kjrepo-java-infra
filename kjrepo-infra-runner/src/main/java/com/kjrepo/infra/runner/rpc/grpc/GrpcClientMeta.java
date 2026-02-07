package com.kjrepo.infra.runner.rpc.grpc;

import org.slf4j.Logger;

import com.annimon.stream.Stream;
import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.cluster.ClusterInfo;
import com.kjrepo.infra.cluster.instance.InstanceInfo;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.gregister.GroupRegister;
import com.kjrepo.infra.gregister.GroupRegisterListener;
import com.kjrepo.infra.gregister.context.GroupRegisterFactory;
import com.kjrepo.infra.register.RegisterEvent;
import com.kjrepo.infra.register.RegisterListener;
import com.kjrepo.infra.runner.rpc.RpcAddressInfo;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClientMeta {

	final Logger logger = LoggerUtils.logger(getClass());
	private final LazySupplier<Cluster<ManagedChannel>> cluster;
	private final GroupRegister<GrpcInfo, RpcAddressInfo> gregister;

	@SuppressWarnings({ "unchecked" })
	public GrpcClientMeta(String key, Class<?> clazz) {
		this.gregister = GroupRegisterFactory.getContext(clazz).getGroupRegister(GrpcInfo.class, RpcAddressInfo.class);
		this.cluster = LazySupplier.wrap(() -> new Cluster<ManagedChannel>(ManagedChannel.class, () -> {
			ClusterInfo<RpcAddressInfo> info = new ClusterInfo<RpcAddressInfo>();
			info.setSelector(gregister.get(key).getSelector());
			info.setInstanceInfos(
					Stream.of(gregister.cget(key)).map(p -> InstanceInfo.of(p.getKey(), p.getValue())).toList());
			return info;
		}, rinfo -> {
			ManagedChannel channel = ManagedChannelBuilder
					.forAddress(((InstanceInfo<RpcAddressInfo>) rinfo).getInfo().getHost(),
							((InstanceInfo<RpcAddressInfo>) rinfo).getInfo().getPort())
					.usePlaintext().build();
			return channel;
		}, res -> {
			res.shutdown();

		}));
		this.gregister.caddListener(key, new GroupRegisterListener() {

			@Override
			public void onCreate(String ckey) {
				cluster.get().add(ckey);
			}

			@Override
			public void onRemove(String ckey) {
				cluster.get().remove(ckey);
			}

		});
		this.gregister.caddListener(key, new RegisterListener<RpcAddressInfo>() {
			@Override
			public void onChange(RegisterEvent<RpcAddressInfo> event) {
				cluster.get().refresh(event.getKey());
			}

		});

	}

	public ManagedChannel channel() {
		return cluster.get().getResource();
	}

}
