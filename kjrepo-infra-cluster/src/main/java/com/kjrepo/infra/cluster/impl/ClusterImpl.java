package com.kjrepo.infra.cluster.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;
import com.annimon.stream.function.ThrowableConsumer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.cluster.info.ClusterInfo;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.cluster.instance.Instance;
import com.kjrepo.infra.cluster.selector.Selector;
import com.kjrepo.infra.common.lazy.LazySupplier;

class ClusterImpl<R, I, C extends ClusterInfo<I>> implements Cluster<R>, AutoCloseable {

	private final LazySupplier<Selector> selector;
	private final Map<String, LazySupplier<Instance<R>>> instance_maps = Maps.newConcurrentMap();
	private final List<LazySupplier<Instance<R>>> instance_list = Lists.newCopyOnWriteArrayList();
	private final LazySupplier<C> cinfo;
	private final Function<InstanceInfo<I>, R> mapper;
	private final ThrowableConsumer<R, Exception> release;

	public ClusterImpl(Supplier<C> cinfo, Function<InstanceInfo<I>, R> mapper,
			ThrowableConsumer<R, Exception> release) {
		try {
			this.cinfo = LazySupplier.wrap(() -> {
				cinfo.get().init();
				return cinfo.get();
			});
			this.mapper = mapper;
			this.release = release;
			Stream.of(this.cinfo.get().getInstanceInfos()).map(InstanceInfo::getName).forEach(name -> {
				LazySupplier<Instance<R>> instance = LazySupplier.wrap(() -> {
					InstanceInfo<I> iinfo = Stream.of(this.cinfo.get().getInstanceInfos())
							.collect(Collectors.toMap(i -> i.getName(), i -> i)).get(name);
					if (iinfo == null) {
						return null;
					}
					return Instance.of(name, mapper.apply(iinfo), release);
				});
				if (this.instance_maps.putIfAbsent(name, instance) == null) {
					this.instance_list.add(instance);
				} else {
					throw new RuntimeException("duplicate instance name!!!");
				}
			});
			this.selector = LazySupplier.wrap(() -> this.cinfo.get().getSelector());
		} catch (IllegalArgumentException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public R getResource() {
		return getResource(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
	}

	public R getResource(Long key) {
		return this.selector.get().select(Stream.of(this.instance_list).map(LazySupplier::get).toList(), key).get();
	}

	public void add(String key) {
		this.cinfo.refresh();
		LazySupplier<Instance<R>> instance = LazySupplier.wrap(() -> {
			InstanceInfo<I> iinfo = Stream.of(cinfo.get().getInstanceInfos())
					.collect(Collectors.toMap(i -> i.getName(), i -> i)).get(key);
			if (iinfo == null) {
				return null;
			}
			return Instance.of(iinfo.getName(), mapper.apply(iinfo), this.release);
		});
		if (this.instance_maps.putIfAbsent(key, instance) == null) {
			this.instance_list.add(instance);
		}
	}

	public void remove(String key) {
		this.cinfo.refresh();
		LazySupplier<Instance<R>> instance = this.instance_maps.remove(key);
		if (instance != null) {
			instance_list.remove(instance);
			instance.refresh(Instance::close);
		}
	}

	public void refresh() {
		this.selector.refresh();
	}

	public void refresh(String key) {
		this.cinfo.refresh();
		LazySupplier<Instance<R>> instance = this.instance_maps.get(key);
		if (instance != null) {
			instance.refresh(Instance::close);
		}
	}

	@Override
	public void close() {
		Stream.of(instance_list).forEach(ins -> {
			ins.refresh(Instance::close);
		});
	}

}