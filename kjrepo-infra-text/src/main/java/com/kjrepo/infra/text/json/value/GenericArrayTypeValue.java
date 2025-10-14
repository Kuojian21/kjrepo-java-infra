package com.kjrepo.infra.text.json.value;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.annimon.stream.Stream;
import com.kjrepo.infra.text.json.ConfigUtils;
import com.kjrepo.infra.text.json.JsonUtils;

public class GenericArrayTypeValue extends AbstractValue<GenericArrayType> {

	public GenericArrayTypeValue(Map<Type, Type> mapper) {
		super(mapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> V value(Object json, GenericArrayType type) throws Exception {
		Type componentType = type.getGenericComponentType();
		if (json instanceof List) {
			List<?> list = (List<?>) json;
			if (list.size() == 0) {
				return null;
			}
			Object[] objs = Stream.of(list).map(o -> ConfigUtils.valueUnchecked(o, componentType, this.mapper()))
					.toArray();
			Object[] rtn = (Object[]) Array.newInstance(objs[0].getClass(), objs.length);
			for (int i = 0; i < rtn.length; i++) {
				rtn[i] = objs[i];
			}
			return (V) rtn;
		} else {
			return JsonUtils.value(json, type);
		}
	}

}
