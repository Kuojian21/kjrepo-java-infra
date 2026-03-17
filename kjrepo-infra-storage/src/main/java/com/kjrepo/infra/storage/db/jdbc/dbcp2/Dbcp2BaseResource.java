package com.kjrepo.infra.storage.db.jdbc.dbcp2;

import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.kjrepo.infra.storage.db.jdbc.KjdbcResource;
import com.kjrepo.infra.storage.db.utils.DB_tag_utils;
import com.kjrepo.infra.text.json.ConfigUtils;

@SuppressWarnings("unchecked")
public interface Dbcp2BaseResource extends KjdbcResource<Object> {

	@Override
	default DataSource dataSource(Object obj) {
		Map<String, Object> config = (Map<String, Object>) obj;
		BasicDataSource dataSource = new BasicDataSource();
		ConfigUtils.config(dataSource, config);
		return dataSource;
	}

	@Override
	default String tag(Object obj) {
		Map<String, Object> config = (Map<String, Object>) obj;
		return DB_tag_utils.tag(config.get("url").toString());
	}
}
