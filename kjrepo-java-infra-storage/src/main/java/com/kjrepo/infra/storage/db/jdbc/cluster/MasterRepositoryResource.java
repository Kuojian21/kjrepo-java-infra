package com.kjrepo.infra.storage.db.jdbc.cluster;

import java.util.Map;

import com.annimon.stream.function.Function;
import com.google.common.collect.Maps;
import com.kjrepo.infra.storage.db.jdbc.KjdbcRepository;
import com.kjrepo.infra.storage.db.jdbc.KjdbcResource;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.cluster.resource.MasterResource;
import com.kjrepo.infra.storage.db.jdbc.KjdbcHolder;

public interface MasterRepositoryResource<I, C extends MasterRepositoryInfo<I>>
		extends KjdbcResource<I>, MasterResource<KjdbcHolder, I, C> {

	Map<MasterRepositoryResource<?, ?>, KjdbcRepository> repos = Maps.newConcurrentMap();

	@Override
	default Function<InstanceInfo<I>, KjdbcHolder> mapper() {
		return config -> KjdbcHolder.of(dataSource(config.getInfo()), tag(config.getInfo()));
	}

	default KjdbcRepository getRepository() {
		return repos.computeIfAbsent(this, key -> new KjdbcRepository() {
			@Override
			public KjdbcHolder holder(boolean master) {
				return master ? MasterRepositoryResource.this.getResource().master()
						: MasterRepositoryResource.this.getResource().slave();
			}
		});
	}
}
