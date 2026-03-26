package com.kjrepo.infra.storage.db.jdbc.druid;

import com.kjrepo.infra.storage.db.jdbc.cluster.MasterRepositoryResource;

public interface DruidMasterRepositoryResource
		extends DruidBaseResource, MasterRepositoryResource<Object, DruidMasterRepositoryInfo> {

}
