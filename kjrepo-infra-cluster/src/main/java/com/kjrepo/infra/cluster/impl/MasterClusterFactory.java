package com.kjrepo.infra.cluster.impl;

import java.util.Map;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.ThrowableConsumer;
import com.kjrepo.infra.cluster.MasterCluster;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.cluster.info.MasterClusterInfo;
import com.kjrepo.infra.cluster.info.MasterInfo;
import com.kjrepo.infra.cluster.utils.InfoObjectEquals;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.RegisterEvent;
import com.kjrepo.infra.register.RegisterListener;
import com.kjrepo.infra.register.context.RegisterFactory;

public class MasterClusterFactory {

	public static <R, I, C extends MasterClusterInfo<I>> MasterCluster<R> cluster(Class<C> cclazz, String key,
			Function<InstanceInfo<I>, R> mapper, ThrowableConsumer<R, Exception> release) {
		return cluster(RegisterFactory.getContext().getRegister(cclazz), key, mapper, release);
	}

	public static <R, I, C extends MasterClusterInfo<I>> MasterCluster<R> cluster(Register<C> register, String key,
			Function<InstanceInfo<I>, R> mapper, ThrowableConsumer<R, Exception> release) {
		LazySupplier<C> info = LazySupplier.wrap(() -> register.get(key));
		LazySupplier<MasterClusterImpl<R, I, C>> cluster = LazySupplier
				.wrap(() -> new MasterClusterImpl<R, I, C>(info, mapper, release));
		Object lock = new Object();
		register.addListener(key, new RegisterListener<C>() {
			@Override
			public void onChange(RegisterEvent<C> event) {
				synchronized (lock) {
					MasterClusterInfo<?> oData = (MasterClusterInfo<?>) info.get();
					info.refresh();
					MasterClusterInfo<?> nData = (MasterClusterInfo<?>) info.get();
					Map<String, MasterInfo<?>> oMap = Stream.of(oData.getInstanceInfos())
							.collect(Collectors.toMap(i -> i.getName(), i -> i.getInfo()));
					Map<String, MasterInfo<?>> nMap = Stream.of(nData.getInstanceInfos())
							.collect(Collectors.toMap(i -> i.getName(), i -> i.getInfo()));

					oMap.forEach((name, oInfo) -> {
						if (!nMap.containsKey(name)) {
							cluster.get().remove(name);
						} else {
							MasterInfo<?> nInfo = nMap.get(name);
							if (InfoObjectEquals.equals(oInfo, nInfo)) {

							} else {
								Object om = oInfo.getMaster();
								Object nm = nInfo.getMaster();
								if (InfoObjectEquals.equals(om, nm)) {

								} else {
									cluster.get().refresh(name);
								}
								Map<String, ?> osMap = Stream.of(oInfo.getSlaves().getInstanceInfos())
										.collect(Collectors.toMap(i -> i.getName(), i -> i.getInfo()));
								Map<String, ?> nsMap = Stream.of(nInfo.getSlaves().getInstanceInfos())
										.collect(Collectors.toMap(i -> i.getName(), i -> i.getInfo()));
								osMap.forEach((sname, soinfo) -> {
									if (!nsMap.containsKey(sname)) {
										cluster.get().remove(name, sname);
									} else {
										Object sninfo = nsMap.get(sname);
										if (InfoObjectEquals.equals(soinfo, sninfo)) {

										} else {
											cluster.get().refresh(name, sname);
										}
										nsMap.remove(sname);
									}
								});
								nsMap.forEach((sname, sninfo) -> {
									cluster.get().add(name, sname);
								});
							}
							nMap.remove(name);
						}
					});
					nMap.forEach((name, iInfo) -> {
						cluster.get().add(key);
					});
				}
			}
		});
		synchronized (lock) {
			return cluster.get();
		}

	}

}
