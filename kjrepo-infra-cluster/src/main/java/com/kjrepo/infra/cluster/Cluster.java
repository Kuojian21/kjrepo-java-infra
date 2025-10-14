package com.kjrepo.infra.cluster;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.cluster.instance.Instance;
import com.kjrepo.infra.cluster.instance.InstanceInfo;
import com.kjrepo.infra.cluster.selector.RandomSelector;
import com.kjrepo.infra.cluster.selector.Selector;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class Cluster<R> implements Closeable {

	private final LazySupplier<Selector<R>> selector;
	private final Map<String, LazySupplier<Instance<R>>> instanceMap = Maps.newConcurrentMap();
	private final List<LazySupplier<Instance<R>>> instanceList = Lists.newCopyOnWriteArrayList();
	private final LazySupplier<ClusterInfo<?>> info;
	private final Class<R> clazz;
	private final Function<?, R> mapper;
	@SuppressWarnings("unused")
	private final Consumer<R> release;

	@SuppressWarnings("unchecked")
	public Cluster(Class<R> clazz, Supplier<ClusterInfo<?>> cinfo, Function<?, R> mapper, Consumer<R> release) {
		try {
			this.info = LazySupplier.wrap(() -> cinfo.get().init());
			this.clazz = clazz;
			this.mapper = mapper;
			this.release = release;
			Stream.of(info.get().getInstanceInfos()).forEach(iInfo -> {
				LazySupplier<Instance<R>> instance = LazySupplier.wrap(() -> {
					InstanceInfo<?> tInfo = Stream.of(info.get().getInstanceInfos())
							.collect(Collectors.toMap(i -> i.getName(), i -> i)).get(iInfo.getName());
					return Instance.of(tInfo.getName(), clazz, ((Function<InstanceInfo<?>, R>) mapper).apply(tInfo),
							release);
				});
				if (this.instanceMap.putIfAbsent(iInfo.getName(), instance) == null) {
					this.instanceList.add(instance);
				} else {
					throw new RuntimeException("duplicate instance name!!!");
				}
			});
			this.selector = LazySupplier.wrap(() -> {
				try {
					if (StringUtils.isNotEmpty(info.get().getSelector())) {
						return (Selector<R>) Class.forName(info.get().getSelector())
								.getDeclaredConstructor(new Class<?>[] { List.class })
								.newInstance(new Object[] { instanceList });
					} else {
						return new RandomSelector<R>(instanceList);
					}
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException
						| ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (IllegalArgumentException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public R getResource() {
		return this.selector.get().select().get();
	}

	public R getResource(Object... args) {
		return this.selector.get().select(args).get();
	}

	@SuppressWarnings("unchecked")
	public void add(String key) {
		this.info.refresh();
		LazySupplier<Instance<R>> instance = LazySupplier.wrap(() -> {
			InstanceInfo<?> tInfo = Stream.of(info.get().getInstanceInfos())
					.collect(Collectors.toMap(i -> i.getName(), i -> i)).get(key);
			return Instance.of(tInfo.getName(), clazz, ((Function<Object, R>) mapper).apply((Object) tInfo.getInfo()));
		});
		if (this.instanceMap.putIfAbsent(key, instance) == null) {
			this.instanceList.add(instance);
		}
	}

	public void remove(String key) {
		this.info.refresh();
		LazySupplier<Instance<R>> instance = this.instanceMap.remove(key);
		if (instance != null) {
			instanceList.remove(instance);
			if (instance.isInited()) {
				instance.get().close();
			}
		}
	}

	public void refresh() {
		this.selector.refresh();
	}

	public void refresh(String key) {
		this.info.refresh();
		LazySupplier<Instance<R>> instance = this.instanceMap.get(key);
		if (instance.isInited()) {
			Instance<R> old = instance.get();
			instance.refresh();
			old.close();
		}
	}

	@Override
	public void close() {
		Stream.of(instanceList).forEach(ins -> {
			if (ins.isInited()) {
				try {
					ins.get().close();
				} catch (Exception e) {
					LoggerUtils.logger(getClass()).error("", e);
				}
			}
		});
	}

}
