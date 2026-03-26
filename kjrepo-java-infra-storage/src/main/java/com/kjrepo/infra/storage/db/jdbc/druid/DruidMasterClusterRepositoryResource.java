package com.kjrepo.infra.storage.db.jdbc.druid;

import com.kjrepo.infra.storage.db.jdbc.cluster.MasterClusterRepositoryResource;

public interface DruidMasterClusterRepositoryResource
		extends DruidBaseResource, MasterClusterRepositoryResource<Object, DruidMasterClusterRepositoryInfo> {

}
