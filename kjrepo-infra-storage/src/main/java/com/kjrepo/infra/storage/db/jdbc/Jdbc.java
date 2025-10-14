package com.kjrepo.infra.storage.db.jdbc;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.google.common.collect.Lists;
import com.kjrepo.infra.storage.db.model.Model;
import com.kjrepo.infra.storage.db.sql.SqlWhereBuilder;

public interface Jdbc<T> {

	default int insert(T data) {
		return insert(Lists.newArrayList(data));
	}

	int insert(List<T> data);

	default int update(Map<String, Object> values, Map<String, Object> params) {
		return update(values, SqlWhereBuilder.and(model()).expr(params));
	}

	int update(Map<String, Object> values, SqlWhereBuilder sqlWhereBuilder);

	default int delete(Map<String, Object> params) {
		return delete(SqlWhereBuilder.and(model()).expr(params));
	}

	int delete(SqlWhereBuilder sqlWhereBuilder);

	default List<T> select(Map<String, Object> params) {
		return select(SqlWhereBuilder.and(model()).expr(params));
	}

	List<T> select(SqlWhereBuilder sqlWhereBuilder);

	NamedParameterJdbcOperations jdbcTemplate();

	Model model();

	boolean create();

}
