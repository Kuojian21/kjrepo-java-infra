package com.kjrepo.infra.text.json.value;

import java.lang.reflect.Type;

public interface Value<T extends Type> {

	<V> V value(Object json, T type) throws Exception;

	default <V> V valueUnchecked(Object json, T type) {
		try {
			return value(json, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
