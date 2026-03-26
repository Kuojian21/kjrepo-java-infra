package com.kjrepo.infra.storage.db.jdbc.druid;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.kjrepo.infra.storage.db.jdbc.KjdbcResource;
import com.kjrepo.infra.storage.db.utils.DB_tag_utils;
import com.kjrepo.infra.text.json.ConfigUtils;

@SuppressWarnings("unchecked")
public interface DruidBaseResource extends KjdbcResource<Object> {

	@Override
	default DataSource dataSource(Object obj) {
		try {
			Map<String, Object> config = (Map<String, Object>) obj;
			DruidDataSource dataSource = new DruidDataSource();
			ConfigUtils.config(dataSource, config);
			dataSource.init();
			return dataSource;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	default String tag(Object obj) {
		Map<String, Object> config = (Map<String, Object>) obj;
		return DB_tag_utils.tag(config.get("url").toString());
	}
}
