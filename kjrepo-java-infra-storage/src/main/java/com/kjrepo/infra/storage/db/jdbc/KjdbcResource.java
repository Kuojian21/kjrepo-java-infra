package com.kjrepo.infra.storage.db.jdbc;

import javax.sql.DataSource;

public interface KjdbcResource<I> {

	DataSource dataSource(I info);

	String tag(I info);

}
