package com.kjrepo.infra.common.hook;

import java.util.Map;

import com.annimon.stream.Optional;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.function.ThrowableRunnable;
import com.kjrepo.infra.common.lazy.LazySupplier;

public class HookHelper {

	private static final Map<String, LazySupplier<Hooks>> hooks = Maps.newConcurrentMap();

	public static void addHook(String module, ThrowableRunnable<? extends Throwable> hook) {
		hooks.computeIfAbsent(Optional.ofNullable(module).orElse("def"), k -> LazySupplier.wrap(() -> new Hooks(k)))
				.get().add(hook);
	}

}
