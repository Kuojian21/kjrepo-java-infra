package com.kjrepo.infra.cluster.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.ThrowableConsumer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.cluster.Master;
import com.kjrepo.infra.cluster.MasterCluster;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.cluster.info.MasterClusterInfo;
import com.kjrepo.infra.cluster.info.MasterInfo;
import com.kjrepo.infra.cluster.instance.Instance;
import com.kjrepo.infra.cluster.selector.Selector;
import com.kjrepo.infra.common.lazy.LazySupplier;

class MasterClusterImpl<R, I, S extends MasterClusterInfo<I>> implements MasterCluster<R>, AutoCloseable {

	private final LazySupplier<Selector> selector;
	private final Map<String, LazySupplier<Instance<MasterImpl<R, I, MasterInfo<I>>>>> instance_maps = Maps
			.newConcurrentMap();
	private final List<LazySupplier<Instance<MasterImpl<R, I, MasterInfo<I>>>>> instance_list = Lists
			.newCopyOnWriteArrayList();
	private final LazySupplier<S> cinfo;
	private final Function<InstanceInfo<I>, R> mapper;
	private final ThrowableConsumer<R, Exception> release;

	public MasterClusterImpl(LazySupplier<S> cinfo, Function<InstanceInfo<I>, R> mapper,
			ThrowableConsumer<R, Exception> release) {
		this.cinfo = cinfo;
		this.mapper = mapper;
		this.release = release;
		Stream.of(this.cinfo.get().getInstanceInfos()).map(InstanceInfo::getName).forEach(name -> {
			LazySupplier<Instance<MasterImpl<R, I, MasterInfo<I>>>> instance = LazySupplier.wrap(() -> {
				LazySupplier<MasterInfo<I>> iinfo = LazySupplier
						.wrap(() -> Stream.of(this.cinfo.get().getInstanceInfos())
								.collect(Collectors.toMap(i -> i.getName(), i -> i)).get(name).getInfo());
				return Instance.of(name, new MasterImpl<R, I, MasterInfo<I>>(iinfo, mapper, release),
						MasterImpl::close);
			});
			if (this.instance_maps.putIfAbsent(name, instance) == null) {
				this.instance_list.add(instance);
			} else {
				throw new RuntimeException("duplicate instance name!!!");
			}
		});
		this.selector = LazySupplier.wrap(() -> this.cinfo.get().getSelector());
	}

	public Master<R> getResource() {
		return getResource(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
	}

	public Master<R> getResource(Long key) {
		return this.selector.get().select(Stream.of(this.instance_list).map(LazySupplier::get).toList(), key).get();
	}

	public void add(String key) {
		LazySupplier<Instance<MasterImpl<R, I, MasterInfo<I>>>> instance = LazySupplier.wrap(() -> {
			LazySupplier<MasterInfo<I>> iinfo = LazySupplier.wrap(() -> Stream.of(this.cinfo.get().getInstanceInfos())
					.collect(Collectors.toMap(i -> i.getName(), i -> i)).get(key).getInfo());
			return Instance.of(key, new MasterImpl<R, I, MasterInfo<I>>(iinfo, mapper, release), MasterImpl::close);
		});
		if (this.instance_maps.putIfAbsent(key, instance) == null) {
			this.instance_list.add(instance);
		}
	}

	public void add(String key, String sKey) {
		LazySupplier<Instance<MasterImpl<R, I, MasterInfo<I>>>> instance = this.instance_maps.get(key);
		if (instance != null) {
			instance.acceptIfInitedInLock(ins -> ins.get().add(sKey));
		}
	}

	public void remove(String key) {
		LazySupplier<Instance<MasterImpl<R, I, MasterInfo<I>>>> instance = this.instance_maps.remove(key);
		if (instance != null) {
			this.instance_list.remove(instance);
			instance.refresh(Instance::close);
		}
	}

	public void remove(String key, String sKey) {
		LazySupplier<Instance<MasterImpl<R, I, MasterInfo<I>>>> instance = this.instance_maps.get(key);
		if (instance != null) {
			instance.acceptIfInitedInLock(ins -> ins.get().remove(sKey));
		}
	}

	public void refresh(String key) {
		LazySupplier<Instance<MasterImpl<R, I, MasterInfo<I>>>> instance = this.instance_maps.get(key);
		if (instance != null) {
			instance.acceptIfInitedInLock(ins -> ins.get().refresh());
		}
	}

	public void refresh(String key, String sKey) {
		LazySupplier<Instance<MasterImpl<R, I, MasterInfo<I>>>> instance = this.instance_maps.get(key);
		if (instance != null) {
			instance.acceptIfInitedInLock(ins -> ins.get().refresh(sKey));
		}
	}

	@Override
	public void close() throws Exception {
		Stream.of(this.instance_list).forEach(ins -> {
			ins.refresh(Instance::close);
		});
	}
}