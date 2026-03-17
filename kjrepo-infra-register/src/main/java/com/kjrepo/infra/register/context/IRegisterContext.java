package com.kjrepo.infra.register.context;

import com.kjrepo.infra.common.spi.PkgSpi;
import com.kjrepo.infra.register.Register;

public interface IRegisterContext extends PkgSpi {

	<I> Register<I> getRegister(Class<I> clazz);

}
