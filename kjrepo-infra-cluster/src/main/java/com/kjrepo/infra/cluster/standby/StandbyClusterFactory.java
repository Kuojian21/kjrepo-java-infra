package com.kjrepo.infra.cluster.standby;

import java.util.Map;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.kjrepo.infra.cluster.utils.InfoObjectEquals;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.RegisterEvent;
import com.kjrepo.infra.register.RegisterListener;

public class StandbyClusterFactory {

	@SuppressWarnings("rawtypes")
	public static <R> LazySupplier<StandbyCluster<R>> cluster(Class<R> rclazz, Register<StandbyClusterInfo> register,
			String key, Function<?, R> mapper, Consumer<R> release) {
		LazySupplier<StandbyClusterInfo<?>> info = LazySupplier.wrap(() -> register.get(key));
		LazySupplier<StandbyCluster<R>> cluster = LazySupplier
				.wrap(() -> new StandbyCluster<R>(rclazz, info, mapper, release));
		register.addListener(key, new RegisterListener<>() {
			@Override
			public void onChange(RegisterEvent<StandbyClusterInfo> event) {
				if (!cluster.isInited()) {
					return;
				}

				StandbyClusterInfo<?> oData = (StandbyClusterInfo<?>) info.get();
				info.refresh();
				StandbyClusterInfo<?> nData = (StandbyClusterInfo<?>) info.get();
				Map<String, StandbyInfo<?>> oMap = Stream.of(oData.getInstanceInfos())
						.collect(Collectors.toMap(i -> i.getName(), i -> i.getInfo()));
				Map<String, StandbyInfo<?>> nMap = Stream.of(nData.getInstanceInfos())
						.collect(Collectors.toMap(i -> i.getName(), i -> i.getInfo()));

				oMap.forEach((name, oInfo) -> {
					if (!nMap.containsKey(name)) {
						cluster.get().remove(name);
					} else {
						StandbyInfo<?> nInfo = nMap.get(name);
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
		});
		return cluster;

	}

}
