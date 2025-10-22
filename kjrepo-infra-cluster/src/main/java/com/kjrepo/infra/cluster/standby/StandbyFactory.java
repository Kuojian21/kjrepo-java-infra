package com.kjrepo.infra.cluster.standby;

import java.util.Map;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.kjrepo.infra.cluster.utils.InfoObjectEquals;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.utils.StackUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.RegisterEvent;
import com.kjrepo.infra.register.RegisterListener;
import com.kjrepo.infra.register.context.RegisterFactory;

public class StandbyFactory {

	public static <R, C extends StandbyInfo<?>> LazySupplier<Standby<R>> standby(Class<R> rclazz, Class<C> cclazz,
			String key, Function<?, R> mapper, Consumer<R> release) {
		Register<C> register = RegisterFactory.getContext(StackUtils.firstBusinessInvokerClassname()).getRegister(cclazz);
		LazySupplier<StandbyInfo<?>> info = LazySupplier.wrap(() -> register.get(key));
		LazySupplier<Standby<R>> standby = LazySupplier.wrap(() -> new Standby<R>(rclazz, info, mapper, release));
		register.addListener(key, new RegisterListener<>() {
			@Override
			public void onChange(RegisterEvent<C> event) {
				if (!standby.isInited()) {
					return;
				}
				StandbyInfo<?> oData = (StandbyInfo<?>) info.get();
				info.refresh();
				StandbyInfo<?> nData = (StandbyInfo<?>) info.get();

				Object oMasterInfo = oData.getMaster();
				Object nMasterInfo = nData.getMaster();
				if (!InfoObjectEquals.equals(oMasterInfo, nMasterInfo)) {
					standby.get().refresh();
				}

				Map<String, ?> oMap = Stream.of(oData.getSlaves().getInstanceInfos())
						.collect(Collectors.toMap(i -> i.getName(), i -> i.getInfo()));
				Map<String, ?> nMap = Stream.of(nData.getSlaves().getInstanceInfos())
						.collect(Collectors.toMap(i -> i.getName(), i -> i.getInfo()));
				oMap.forEach((name, iInfo) -> {
					if (!nMap.containsKey(name)) {
						standby.get().remove(name);
					} else {
						if (InfoObjectEquals.equals(iInfo, nMap.get(name))) {

						} else {
							standby.get().refresh(name);
						}
						nMap.remove(name);
					}
				});
				nMap.forEach((name, iInfo) -> {
					standby.get().add(key);
				});
			}
		});
		return standby;
	}

}
