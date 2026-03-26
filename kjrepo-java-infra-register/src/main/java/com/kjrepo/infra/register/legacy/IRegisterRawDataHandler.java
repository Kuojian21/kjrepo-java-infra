package com.kjrepo.infra.register.legacy;

public interface IRegisterRawDataHandler {

	Class<?> forClazz();

	void handle(Object rawData);

}
