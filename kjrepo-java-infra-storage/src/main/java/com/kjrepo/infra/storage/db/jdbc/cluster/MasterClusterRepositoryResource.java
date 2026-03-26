package com.kjrepo.infra.storage.db.jdbc.cluster;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.annimon.stream.function.Function;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.kjrepo.infra.cluster.Master;
import com.kjrepo.infra.cluster.info.InstanceInfo;
import com.kjrepo.infra.cluster.resource.MasterClusterResource;
import com.kjrepo.infra.storage.db.jdbc.KjdbcHolder;
import com.kjrepo.infra.storage.db.jdbc.KjdbcRepository;
import com.kjrepo.infra.storage.db.jdbc.KjdbcResource;

public interface MasterClusterRepositoryResource<I, C extends MasterClusterRepositoryInfo<I>>
		extends KjdbcResource<I>, MasterClusterResource<KjdbcHolder, I, C> {

	Map<MasterClusterRepositoryResource<?, ?>, ConcurrentMap<Master<KjdbcHolder>, KjdbcRepository>> repos = Maps
			.newConcurrentMap();

	@Override
	default Function<InstanceInfo<I>, KjdbcHolder> mapper() {
		return config -> KjdbcHolder.of(dataSource(config.getInfo()), tag(config.getInfo()));
	}

	default KjdbcRepository getRepository(long key) {
		Master<KjdbcHolder> standby = MasterClusterRepositoryResource.this.getResource().getResource(key);
		return repos.computeIfAbsent(this, k -> new MapMaker().weakKeys().weakValues().makeMap())
				.computeIfAbsent(standby, sb -> new KjdbcRepository() {
					@Override
					public KjdbcHolder holder(boolean master) {
						return master ? sb.master() : sb.slave();
					}
				});

	}

}
