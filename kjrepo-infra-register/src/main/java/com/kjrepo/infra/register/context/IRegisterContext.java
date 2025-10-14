package com.kjrepo.infra.register.context;

import com.kjrepo.infra.loader.Loader;
import com.kjrepo.infra.register.Register;

public interface IRegisterContext extends Loader {

	<I> Register<I> getRegister(Class<I> clazz);

	default String pkg() {
		return "";
	}

}
