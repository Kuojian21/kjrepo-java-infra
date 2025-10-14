package com.kjrepo.infra.gregister.context;

import com.kjrepo.infra.gregister.GroupRegister;
import com.kjrepo.infra.loader.Loader;

public interface IGroupRegisterContext extends Loader {

	<V, I> GroupRegister<V, I> getGroupRegister(Class<V> vclass, Class<I> clazz);

	default String pkg() {
		return "";
	}

}
