package com.kjrepo.infra.gregister;

import java.util.List;

import com.kjrepo.infra.common.info.Pair;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.RegisterListener;

public interface GroupRegister<V, I> extends Register<V> {

	List<Pair<String, I>> cget(String pkey);

	void cset(String pkey, String ckey, I value);

	void caddListener(String pkey, GroupRegisterListener listener);

	void caddListener(String pkey, RegisterListener<I> listener);
}
