package com.kjrepo.infra.storage.db.jdbc;

public abstract class DAO<T> extends JdbcImpl<T> implements Jdbc<T> {

	public DAO(Class<T> clazz, String table) {
		super(clazz, table);
	}

	public DAO(Class<T> clazz) {
		super(clazz);
	}

}