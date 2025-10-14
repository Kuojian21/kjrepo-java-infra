package com.kjrepo.infra.gregister;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kjrepo.infra.common.info.Pair;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.RegisterEvent;
import com.kjrepo.infra.register.RegisterListener;

public abstract class AbstractGroupReigster<V, I> implements GroupRegister<V, I> {

	protected final Logger logger = LoggerUtils.logger(getClass());

	private final ConcurrentMap<String, LazySupplier<LazySupplier<List<Pair<String, I>>>>> datas = Maps
			.newConcurrentMap();
	private final ConcurrentMap<String, Set<GroupRegisterListener>> glisteners = Maps.newConcurrentMap();
	private final ConcurrentMap<String, Set<RegisterListener<I>>> rlisteners = Maps.newConcurrentMap();
	private final Register<I> register;

	public AbstractGroupReigster(Register<I> register) {
		this.register = register;
	}

	@Override
	public final List<Pair<String, I>> cget(String pkey) {
		return this.datas.computeIfAbsent(pkey, path -> LazySupplier.wrap(() -> {
			this.init(path);
			return LazySupplier.wrap(() -> data(path));
		})).get().get();
	}

	@Override
	public void cset(String pkey, String ckey, I value) {
		this.register.set(key(pkey, ckey), value);
	}

	@Override
	public void caddListener(String pkey, GroupRegisterListener listener) {
		this.glisteners.computeIfAbsent(pkey, k -> Sets.newConcurrentHashSet()).add(listener);
	}

	@Override
	public void caddListener(String pkey, RegisterListener<I> listener) {
		this.rlisteners.computeIfAbsent(pkey, k -> Sets.newConcurrentHashSet()).add(new RegisterListener<I>() {
			@Override
			public void onChange(RegisterEvent<I> event) {
				crefresh(pkey);
				listener.onChange(event);
			}
		});
		Optional.ofNullable(datas.get(pkey)).ifPresent(data -> data.get().refresh());
	}

	protected abstract void init(String path);

	protected abstract List<Pair<String, I>> data(String path);

	protected I get(String pkey, String ckey) {
		Stream.ofNullable(rlisteners.get(pkey)).forEach(listener -> register.addListener(key(pkey, ckey), listener));
		return this.register.get(key(pkey, ckey));
	}

	private void crefresh(String pkey) {
		Optional.ofNullable(this.datas.get(pkey)).ifPresent(data -> data.get().refresh());
	}

	protected void fireCreate(String pkey, String ckey) {
		crefresh(pkey);
		Stream.ofNullable(this.glisteners.get(pkey)).forEach(listener -> {
			listener.onCreate(ckey);
		});
	}

	protected void fireRemove(String pkey, String ckey) {
		crefresh(pkey);
		Stream.ofNullable(this.glisteners.get(pkey)).forEach(listener -> {
			listener.onRemove(ckey);
		});
	}

	private String key(String pkey, String ckey) {
		return pkey + "/" + ckey;
	}

}
