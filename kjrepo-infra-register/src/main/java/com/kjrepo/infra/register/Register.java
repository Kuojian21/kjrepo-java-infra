package com.kjrepo.infra.register;

public interface Register<V> {

	V get(String key);

	void set(String key, V value);

	void addListener(String key, RegisterListener<V> listener);

}
