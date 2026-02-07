package com.kjrepo.infra.gregister;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kjrepo.infra.common.info.Pair;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.RegisterEvent;
import com.kjrepo.infra.register.RegisterListener;

public abstract class AbstractGroupReigster<V, I> implements GroupRegister<V, I> {

	protected final Logger logger = LoggerUtils.logger(GroupRegister.class);

	private final ConcurrentMap<String, LazySupplier<LazySupplier<List<CPair<I>>>>> datas = Maps.newConcurrentMap();
	private final ConcurrentMap<String, Set<GroupRegisterListener>> pListeners = Maps.newConcurrentMap();
	private final ConcurrentMap<String, Set<RegisterListener<I>>> cListeners = Maps.newConcurrentMap();
	private final Register<I> cregister;

	public AbstractGroupReigster(Register<I> register) {
		this.cregister = register;
	}

	private List<CPair<I>> cpair(String pkey) {
		return this.datas.computeIfAbsent(pkey, path -> LazySupplier.wrap(() -> {
			this.init(path);
			return LazySupplier.wrap(() -> data(path));
		})).get().get();
	}

	@Override
	public final List<Pair<String, I>> cget(String pkey) {
		return Stream.of(cpair(pkey)).map(cp -> Pair.pair(cp.getKey(), cp.getData().get())).toList();

	}

	@Override
	public final List<String> ckeys(String pkey) {
		return Stream.of(cpair(pkey)).map(CPair::getKey).toList();
	}

	@Override
	public void cset(String ckey, I value) {
		this.cregister.set(ckey, value);
	}

	@Override
	public void caddListener(String pkey, GroupRegisterListener listener) {
		logger.info("add listener for [{}]'s gourp!!!", pkey);
		this.pListeners.computeIfAbsent(pkey, k -> Sets.newConcurrentHashSet()).add(listener);
	}

	@Override
	public void caddListener(String pkey, RegisterListener<I> listener) {
		logger.info("add listener for [{}]'s children!!!", pkey);
		this.cListeners.computeIfAbsent(pkey, k -> Sets.newConcurrentHashSet()).add(new RegisterListener<I>() {
			@Override
			public void onChange(RegisterEvent<I> event) {
				crefresh(pkey);
				listener.onChange(event);
			}
		});
		Optional.ofNullable(datas.get(pkey)).ifPresent(data -> data.get().refresh());
	}

	protected abstract void init(String path);

	protected abstract List<CPair<I>> data(String path);

	protected I cgetAndInitListener(String pkey, String ckey) {
		Stream.ofNullable(cListeners.get(pkey)).forEach(listener -> cregister.addListener(ckey, listener));
		return this.cregister.get(ckey);
	}

	private void crefresh(String pkey) {
		Optional.ofNullable(this.datas.get(pkey)).ifPresent(data -> data.get().refresh());
	}

	protected void fireCreate(String pkey, String ckey) {
		logger.info("fireCreate,pkey:[{}] ckey:[{}]!!!", pkey, ckey);
		crefresh(pkey);
		Stream.ofNullable(this.pListeners.get(pkey)).forEach(listener -> {
			listener.onCreate(ckey);
		});
	}

	protected void fireRemove(String pkey, String ckey) {
		logger.info("fireRemove,pkey:[{}] ckey:[{}]!!!", pkey, ckey);
		crefresh(pkey);
		Stream.ofNullable(this.pListeners.get(pkey)).forEach(listener -> {
			listener.onRemove(ckey);
		});
	}

	protected static class CPair<T> {

		public static <T> CPair<T> of(String key, LazySupplier<T> data) {
			return new CPair<>(key, data);
		}

		private final String key;
		private final Supplier<T> data;

		private CPair(String key, Supplier<T> data) {
			super();
			this.key = key;
			this.data = data;
		}

		public String getKey() {
			return key;
		}

		public Supplier<T> getData() {
			return data;
		}

	}

}
