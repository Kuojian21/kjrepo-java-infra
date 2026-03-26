package com.kjrepo.infra.register.group.context;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.kjrepo.infra.register.group.GroupRegister;

public abstract class AbstractGroupRegisterContext implements GroupRegisterContext {

	private final ConcurrentMap<Class<?>, GroupRegister<?, ?>> registers = Maps.newConcurrentMap();

	@SuppressWarnings("unchecked")
	@Override
	public final <V, I> GroupRegister<V, I> getGroupRegister(Class<V> vclass, Class<I> clazz) {
		return (GroupRegister<V, I>) registers.computeIfAbsent(clazz, cl -> newGroupRegister(vclass, clazz));
	}

	public abstract <V, I> GroupRegister<V, I> newGroupRegister(Class<V> vclass, Class<I> clazz);

}