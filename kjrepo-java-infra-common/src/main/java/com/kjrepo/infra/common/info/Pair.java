package com.kjrepo.infra.common.info;

import java.util.Objects;

public class Pair<K, V> {

	public static <K, V> Pair<K, V> pair(K key, V value) {
		Pair<K, V> pair = new Pair<K, V>();
		pair.key = key;
		pair.value = value;
		return pair;
	}

	private K key;
	private V value;

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair<K, V> other = (Pair<K, V>) obj;
		return Objects.equals(key, other.key) && Objects.equals(value, other.value);
	}

}
