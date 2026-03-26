package com.kjrepo.infra.register.group.context;

import com.kjrepo.infra.common.spi.PkgSpi;
import com.kjrepo.infra.register.group.GroupRegister;

public interface GroupRegisterContext extends PkgSpi {

	<V, I> GroupRegister<V, I> getGroupRegister(Class<V> vclass, Class<I> clazz);

}
