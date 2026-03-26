package com.kjrepo.infra.storage.db.jdbc.dbcp2;

import com.kjrepo.infra.storage.db.jdbc.cluster.MasterRepositoryResource;

public interface Dbcp2MasterRepositoryResource
		extends Dbcp2BaseResource, MasterRepositoryResource<Object, Dbcp2MasterRepositoryInfo> {

}
