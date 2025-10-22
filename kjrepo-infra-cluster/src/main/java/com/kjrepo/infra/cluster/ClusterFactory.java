package com.kjrepo.infra.cluster;

import java.util.Map;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.kjrepo.infra.cluster.instance.InstanceInfo;
import com.kjrepo.infra.cluster.utils.InfoObjectEquals;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.utils.StackUtils;
import com.kjrepo.infra.gregister.GroupRegister;
import com.kjrepo.infra.gregister.GroupRegisterListener;
import com.kjrepo.infra.gregister.context.GroupRegisterFactory;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.RegisterEvent;
import com.kjrepo.infra.register.RegisterListener;
import com.kjrepo.infra.register.context.RegisterFactory;

public class ClusterFactory {

	public static <R, I, C extends ClusterInfo<I>> LazySupplier<Cluster<R>> gcluster(Class<R> rclazz, Class<C> cclazz,
			Class<I> iclazz, String key, Function<InstanceInfo<I>, R> mapper, Consumer<R> release) {
		GroupRegister<C, I> gregister = GroupRegisterFactory.getContext(StackUtils.firstBusinessInvokerClassname())
				.getGroupRegister(cclazz, iclazz);
		LazySupplier<ClusterInfo<?>> info = LazySupplier.wrap(() -> {
			ClusterInfo<I> cinfo = gregister.get(key);
			cinfo.setInstanceInfos(
					Stream.of(gregister.cget(key)).map(p -> InstanceInfo.of(p.getKey(), p.getValue())).toList());
			return cinfo;
		});
		LazySupplier<Cluster<R>> cluster = LazySupplier.wrap(() -> new Cluster<R>(rclazz, info, mapper, release));
		gregister.addListener(key, new RegisterListener<>() {
			@Override
			public void onChange(RegisterEvent<C> event) {
				info.refresh();
				cluster.get().refresh();
			}
		});
		gregister.caddListener(key, new GroupRegisterListener() {

			@Override
			public void onCreate(String ckey) {
				info.refresh();
				cluster.get().add(ckey);
			}

			@Override
			public void onRemove(String ckey) {
				info.refresh();
				cluster.get().remove(ckey);
			}

		});
		gregister.caddListener(key, new RegisterListener<>() {

			@Override
			public void onChange(RegisterEvent<I> event) {
				info.refresh();
				cluster.get().refresh(event.getKey().replace(key + "/", ""));
			}

		});
		return cluster;
	}

	public static <R, I, C extends ClusterInfo<I>> LazySupplier<Cluster<R>> cluster(Class<R> rclazz, Class<C> cclazz,
			String key, Function<InstanceInfo<I>, R> mapper, Consumer<R> release) {
		Register<C> register = RegisterFactory.getContext(StackUtils.firstBusinessInvokerClassname()).getRegister(cclazz);
		LazySupplier<ClusterInfo<?>> info = LazySupplier.wrap(() -> register.get(key));
		LazySupplier<Cluster<R>> cluster = LazySupplier.wrap(() -> new Cluster<R>(rclazz, info, mapper, release));
		register.addListener(key, new RegisterListener<>() {
			@Override
			public void onChange(RegisterEvent<C> event) {
				if (!cluster.isInited()) {
					return;
				}
				ClusterInfo<?> oData = (ClusterInfo<?>) info.get();
				info.refresh();
				cluster.get().refresh();
				ClusterInfo<?> nData = (ClusterInfo<?>) info.get();
				Map<String, ?> oMap = Stream.of(oData.getInstanceInfos())
						.collect(Collectors.toMap(i -> i.getName(), i -> i.getInfo()));
				Map<String, ?> nMap = Stream.of(nData.getInstanceInfos())
						.collect(Collectors.toMap(i -> i.getName(), i -> i.getInfo()));
				oMap.forEach((name, iInfo) -> {
					if (!nMap.containsKey(name)) {
						cluster.get().remove(name);
					} else {
						if (InfoObjectEquals.equals(iInfo, nMap.get(name))) {

						} else {
							cluster.get().refresh(name);
						}
						nMap.remove(name);
					}
				});
				nMap.forEach((name, iInfo) -> {
					cluster.get().add(key);
				});
			}
		});
		return cluster;
	}

}
