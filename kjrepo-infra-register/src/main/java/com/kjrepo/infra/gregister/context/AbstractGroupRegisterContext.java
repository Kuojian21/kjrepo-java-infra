package com.kjrepo.infra.gregister.context;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.kjrepo.infra.gregister.GroupRegister;

public abstract class AbstractGroupRegisterContext implements IGroupRegisterContext {

	private final ConcurrentMap<Class<?>, GroupRegister<?, ?>> registers = Maps.newConcurrentMap();

	@SuppressWarnings("unchecked")
	@Override
	public <V, I> GroupRegister<V, I> getGroupRegister(Class<V> vclass, Class<I> clazz) {
		return (GroupRegister<V, I>) registers.computeIfAbsent(clazz, cl -> newGroupRegister(vclass, clazz));
	}

	public abstract <V, I> GroupRegister<V, I> newGroupRegister(Class<V> vclass, Class<I> clazz);

}