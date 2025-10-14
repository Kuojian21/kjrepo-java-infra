package com.kjrepo.infra.cluster.standby;

import java.io.Closeable;

import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.cluster.instance.Instance;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;

public class Standby<R> implements Closeable {

	private final LazySupplier<Instance<R>> master;
	private final Cluster<R> slaves;

	@SuppressWarnings("unchecked")
	public Standby(Class<R> clazz, LazySupplier<StandbyInfo<?>> info, Function<?, R> mapper, Consumer<R> release) {
		super();
		this.master = LazySupplier.wrap(() -> Instance.of("master", clazz,
				((Function<Object, R>) mapper).apply(info.get().getMaster()), release));
		this.slaves = new Cluster<R>(clazz, LazySupplier.wrap(() -> info.get().getSlaves()), mapper, release);
	}

	public R master() {
		return this.master.get().get();
	}

	public R slave() {
		return this.slaves.getResource();
	}

	public void refresh() {
		if (master.isInited()) {
			Instance<R> instance = this.master.get();
			this.master.refresh();
			instance.close();
		}
	}

	public void refresh(String key) {
		if (master.isInited()) {
			this.master.refresh();
		}
	}

	public void add(String key) {
		this.slaves.add(key);
	}

	public void remove(String key) {
		this.slaves.remove(key);
	}

	@Override
	public void close() {
		if (master.isInited()) {
			try {
				master.get().close();
			} catch (Exception e) {
				LoggerUtils.logger(getClass()).error("", e);
			}
		}
		slaves.close();
	}

}
