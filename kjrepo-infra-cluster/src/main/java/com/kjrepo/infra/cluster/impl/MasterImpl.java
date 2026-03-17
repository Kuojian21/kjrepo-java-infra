package com.kjrepo.infra.cluster.impl;

import com.annimon.stream.function.Function;
import com.annimon.stream.function.ThrowableConsumer;
import com.kjrepo.infra.cluster.Master;
import com.kjrepo.infra.cluster.info.ClusterInfo;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.cluster.info.MasterInfo;
import com.kjrepo.infra.cluster.instance.Instance;
import com.kjrepo.infra.common.lazy.LazySupplier;

class MasterImpl<R, I, S extends MasterInfo<I>> implements Master<R>, AutoCloseable {

	private final LazySupplier<Instance<R>> master;
	private final ClusterImpl<R, I, ClusterInfo<I>> slaves;

	public MasterImpl(LazySupplier<S> sinfo, Function<InstanceInfo<I>, R> mapper,
			ThrowableConsumer<R, Exception> release) {
		super();
		this.master = LazySupplier.wrap(() -> Instance.of("master", mapper.apply(sinfo.get().getMaster()), release));
		this.slaves = new ClusterImpl<R, I, ClusterInfo<I>>(LazySupplier.wrap(() -> sinfo.get().getSlaves()), mapper,
				release);
	}

	public R master() {
		return this.master.get().get();
	}

	public R slave() {
		return this.slaves.getResource();
	}

	public void refresh() {
		master.refresh(Instance::close);
	}

	public void refresh(String key) {
		this.slaves.refresh(key);
	}

	public void add(String key) {
		this.slaves.add(key);
	}

	public void remove(String key) {
		this.slaves.remove(key);
	}

	@Override
	public void close() throws Exception {
		master.refresh(Instance::close);
		slaves.close();
	}

}