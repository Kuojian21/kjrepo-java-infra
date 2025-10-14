package com.kjrepo.infra.cluster.standby;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.cluster.selector.ClusterStandbySelector;
import com.kjrepo.infra.cluster.selector.RandomClusterStandbySelector;
import com.kjrepo.infra.common.lazy.LazySupplier;

public class StandbyCluster<R> {

	private final ClusterStandbySelector<R> selector;
	private final Map<String, LazySupplier<Standby<R>>> instanceMap = Maps.newConcurrentMap();
	private final List<LazySupplier<Standby<R>>> instanceList = Lists.newCopyOnWriteArrayList();
	private final LazySupplier<StandbyClusterInfo<?>> info;
	private final Class<R> clazz;
	private final Function<?, R> mapper;
	private final Consumer<R> release;

	@SuppressWarnings("unchecked")
	public StandbyCluster(Class<R> clazz, LazySupplier<StandbyClusterInfo<?>> info, Function<?, R> mapper,
			Consumer<R> release) {
		try {
			this.info = info;
			this.clazz = clazz;
			this.mapper = mapper;
			this.release = release;
			Stream.of(info.get().getInstanceInfos()).forEach(iInfo -> {
				LazySupplier<Standby<R>> instance = LazySupplier.wrap(() -> {
					LazySupplier<StandbyInfo<?>> tInfo = LazySupplier.wrap(() -> Stream.of(info.get().getInstanceInfos())
							.collect(Collectors.toMap(i -> i.getName(), i -> i)).get(iInfo.getName()).getInfo());
					return new Standby<R>(clazz, tInfo, mapper, release);
				});
				if (this.instanceMap.putIfAbsent(iInfo.getName(), instance) == null) {
					this.instanceList.add(instance);
				} else {
					throw new RuntimeException("duplicate instance name!!!");
				}
			});
			if (StringUtils.isNotEmpty(info.get().getSelector())) {
				this.selector = (ClusterStandbySelector<R>) Class.forName(info.get().getSelector())
						.getDeclaredConstructor(new Class<?>[] { List.class })
						.newInstance(new Object[] { instanceList });
			} else {
				this.selector = new RandomClusterStandbySelector<R>(instanceList);
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public Standby<R> getResource() {
		return this.selector.select();
	}

	public Standby<R> getResource(Object... args) {
		return this.selector.select(args);
	}

	public void add(String key) {
		LazySupplier<Standby<R>> instance = LazySupplier.wrap(() -> {
			LazySupplier<StandbyInfo<?>> tInfo = LazySupplier.wrap(() -> Stream.of(info.get().getInstanceInfos())
					.collect(Collectors.toMap(i -> i.getName(), i -> i)).get(key).getInfo());
			return new Standby<R>(clazz, tInfo, mapper, release);
		});
		if (this.instanceMap.putIfAbsent(key, instance) == null) {
			this.instanceList.add(instance);
		}
	}

	public void add(String key, String sKey) {
		LazySupplier<Standby<R>> instance = this.instanceMap.get(key);
		if (instance != null && instance.isInited()) {
			instance.get().add(sKey);
		}
	}

	public void remove(String key) {
		LazySupplier<Standby<R>> instance = this.instanceMap.remove(key);
		if (instance != null) {
			instanceList.remove(instance);
			if (instance.isInited()) {
				instance.get().close();
			}
		}
	}

	public void remove(String key, String sKey) {
		LazySupplier<Standby<R>> instance = this.instanceMap.get(key);
		if (instance != null && instance.isInited()) {
			instance.get().remove(sKey);
		}
	}

	public void refresh(String key) {
		LazySupplier<Standby<R>> instance = this.instanceMap.get(key);
		if (instance != null && instance.isInited()) {
			instance.get().refresh();
		}
	}

	public void refresh(String key, String sKey) {
		LazySupplier<Standby<R>> instance = this.instanceMap.get(key);
		if (instance != null && instance.isInited()) {
			instance.get().refresh(sKey);
		}
	}

}
