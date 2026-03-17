package com.kjrepo.infra.storage.db.jdbc.hikari;

import com.kjrepo.infra.storage.db.jdbc.KjdbcRepositoryResource;
import com.zaxxer.hikari.HikariConfig;

public interface HikariRepositoryResource extends HikariBaseResource, KjdbcRepositoryResource<HikariConfig> {

}