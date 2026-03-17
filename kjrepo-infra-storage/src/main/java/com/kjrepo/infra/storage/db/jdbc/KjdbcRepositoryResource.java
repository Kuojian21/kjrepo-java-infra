package com.kjrepo.infra.storage.db.jdbc;

import java.util.Map;

import com.annimon.stream.function.Function;
import com.google.common.collect.Maps;
import com.kjrepo.infra.register.resource.IResource;

public interface KjdbcRepositoryResource<I> extends KjdbcResource<I>, IResource<I, KjdbcHolder> {

	Map<KjdbcRepositoryResource<?>, KjdbcRepository> repos = Maps.newConcurrentMap();

	default Function<I, KjdbcHolder> mapper() {
		return config -> KjdbcHolder.of(dataSource(config), tag(config));
	}

	default KjdbcRepository getRepository() {
		return repos.computeIfAbsent(this, key -> new KjdbcRepository() {
			@Override
			public KjdbcHolder holder(boolean master) {
				return KjdbcRepositoryResource.this.get();
			}
		});
	}
}
