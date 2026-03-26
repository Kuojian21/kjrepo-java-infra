package com.kjrepo.infra.cluster.impl;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.ThrowableConsumer;
import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.cluster.info.ClusterInfo;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.cluster.utils.InfoObjectEquals;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.RegisterEvent;
import com.kjrepo.infra.register.RegisterListener;
import com.kjrepo.infra.register.group.GroupRegister;
import com.kjrepo.infra.register.group.GroupRegisterListener;

public class ClusterFactory {

	public static <R, I, C extends ClusterInfo<I>> Cluster<R> gcluster(GroupRegister<C, I> gregister, String key,
			Function<InstanceInfo<I>, R> mapper, ThrowableConsumer<R, Exception> release) {
		LazySupplier<C> info = LazySupplier.wrap(() -> {
			C cinfo = gregister.get(key);
			cinfo.setInstanceInfos(
					Stream.of(gregister.cget(key)).map(p -> InstanceInfo.of(p.getKey(), p.getValue())).toList());
			return cinfo;
		});
		LazySupplier<ClusterImpl<R, I, C>> cluster = LazySupplier
				.wrap(() -> new ClusterImpl<R, I, C>(info, mapper, release));
		final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		final Lock read = lock.readLock();
		final Lock write = lock.writeLock();
		gregister.addListener(key, new RegisterListener<>() {
			@Override
			public void onChange(RegisterEvent<C> event) {
				try {
					read.lock();
					info.refresh();
					cluster.get().refresh();
				} finally {
					read.unlock();
				}
			}
		});
		gregister.caddListener(key, new GroupRegisterListener() {

			@Override
			public void onCreate(String ckey) {
				try {
					read.lock();
					info.refresh();
					cluster.get().add(ckey);
				} finally {
					read.unlock();
				}
			}

			@Override
			public void onRemove(String ckey) {
				try {
					read.lock();
					info.refresh();
					cluster.get().remove(ckey);
				} finally {
					read.unlock();
				}
			}

		});
		gregister.caddListener(key, new RegisterListener<>() {

			@Override
			public void onChange(RegisterEvent<I> event) {
				try {
					read.lock();
					info.refresh();
					cluster.get().refresh(event.getKey());
				} finally {
					read.unlock();
				}
			}

		});
		try {
			write.lock();
			return cluster.get();
		} finally {
			write.unlock();
		}
	}

	public static <R, I, C extends ClusterInfo<I>> Cluster<R> cluster(Register<C> register, String key,
			Function<InstanceInfo<I>, R> mapper, ThrowableConsumer<R, Exception> release) {
		LazySupplier<C> info = LazySupplier.wrap(() -> register.get(key));
		LazySupplier<ClusterImpl<R, I, C>> cluster = LazySupplier
				.wrap(() -> new ClusterImpl<R, I, C>(info, mapper, release));
		Object lock = new Object();
		register.addListener(key, new RegisterListener<>() {
			@Override
			public void onChange(RegisterEvent<C> event) {
				synchronized (lock) {
					ClusterInfo<?> oData = (ClusterInfo<?>) info.get();
					info.refresh();
					cluster.get().refresh();
					ClusterInfo<?> nData = (ClusterInfo<?>) info.get();
					Map<String, ?> oMap = Stream.of(oData.getInstanceInfos())
							.collect(Collectors.toMap(i -> i.getName(), i -> i.getInfo()));
					Map<String, ?> nMap = Stream.of(nData.getInstanceInfos())
							.collect(Collectors.toMap(i -> i.getName(), i -> i.getInfo()));
					oMap.forEach((name, iInfo) -> {
						if (!nMap.containsKey(name)) {
							cluster.get().remove(name);
						} else {
							if (InfoObjectEquals.equals(iInfo, nMap.get(name))) {

							} else {
								cluster.get().refresh(name);
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
