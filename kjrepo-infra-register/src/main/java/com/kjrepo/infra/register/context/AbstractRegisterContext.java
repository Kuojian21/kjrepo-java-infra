package com.kjrepo.infra.register.context;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.kjrepo.infra.register.Register;

public abstract class AbstractRegisterContext implements IRegisterContext {

	private final ConcurrentMap<Class<?>, Register<?>> registers = Maps.newConcurrentMap();

	@SuppressWarnings("unchecked")
	@Override
	public <I> Register<I> getRegister(Class<I> clazz) {
		return (Register<I>) registers.computeIfAbsent(clazz, cl -> newRegister(clazz));
	}

	public abstract <I> Register<I> newRegister(Class<I> clazz);

}
