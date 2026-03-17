package com.kjrepo.infra.storage.db.jdbc.hikari;

import com.kjrepo.infra.storage.db.jdbc.cluster.MasterRepositoryResource;
import com.zaxxer.hikari.HikariConfig;

public interface HikariMasterRepositoryResource
		extends HikariBaseResource, MasterRepositoryResource<HikariConfig, HikariMasterRepositoryInfo> {

}
