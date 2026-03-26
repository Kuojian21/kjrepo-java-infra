package com.kjrepo.infra.register.context;

import com.kjrepo.infra.common.spi.PkgSpi;
import com.kjrepo.infra.register.Register;

public interface RegisterContext extends PkgSpi {

	<I> Register<I> getRegister(Class<I> clazz);

}
