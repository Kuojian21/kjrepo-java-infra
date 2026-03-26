package com.kjrepo.infra.register;

@FunctionalInterface
public interface RegisterListener<D> {

	void onChange(RegisterEvent<D> event);

}
