package com.kjrepo.infra.cluster.impl;

import java.util.Map;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.ThrowableConsumer;
import com.kjrepo.infra.cluster.Master;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.cluster.info.MasterInfo;
import com.kjrepo.infra.cluster.utils.InfoObjectEquals;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.RegisterEvent;
import com.kjrepo.infra.register.RegisterListener;
import com.kjrepo.infra.register.context.RegisterFactory;

public class MasterFactory {

	public static <R, I, M extends MasterInfo<I>> Master<R> master(Class<M> cclazz, String key,
			Function<InstanceInfo<I>, R> mapper, ThrowableConsumer<R, Exception> release) {
		Register<M> register = RegisterFactory.getContext().getRegister(cclazz);
		LazySupplier<M> info = LazySupplier.wrap(() -> register.get(key));
		LazySupplier<MasterImpl<R, I, M>> master = LazySupplier
				.wrap(() -> new MasterImpl<R, I, M>(info, mapper, release));

		Object lock = new Object();
		register.addListener(key, new RegisterListener<>() {
			@Override
			public void onChange(RegisterEvent<M> event) {
				synchronized (lock) {
					MasterInfo<?> oData = (MasterInfo<?>) info.get();
					info.refresh();
					MasterInfo<?> nData = (MasterInfo<?>) info.get();

					Object oMasterInfo = oData.getMaster();
					Object nMasterInfo = nData.getMaster();
					if (!InfoObjectEquals.equals(oMasterInfo, nMasterInfo)) {
						master.get().refresh();
					}

					Map<String, ?> oMap = Stream.of(oData.getSlaves().getInstanceInfos())
							.collect(Collectors.toMap(i -> i.getName(), i -> i.getInfo()));
					Map<String, ?> nMap = Stream.of(nData.getSlaves().getInstanceInfos())
							.collect(Collectors.toMap(i -> i.getName(), i -> i.getInfo()));
					oMap.forEach((name, iInfo) -> {
						if (!nMap.containsKey(name)) {
							master.get().remove(name);
						} else {
							if (InfoObjectEquals.equals(iInfo, nMap.get(name))) {

							} else {
								master.get().refresh(name);
							}
							nMap.remove(name);
						}
					});
					nMap.forEach((name, iInfo) -> {
						master.get().add(key);
					});
				}
			}
		});
		synchronized (lock) {
			return master.get();
		}
	}

}
