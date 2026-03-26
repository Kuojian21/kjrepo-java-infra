package com.kjrepo.infra.text.json.value;

import java.lang.reflect.Type;

import com.kjrepo.infra.text.json.JsonUtils;

public class JacksonValue implements Value<Type> {

	@Override
	public <V> V value(Object json, Type type) throws Exception {
		return JsonUtils.value(json, type);
	}

}
