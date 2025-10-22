package com.kjrepo.infra.text.json.value;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kjrepo.infra.text.json.ConfigUtils;
import com.kjrepo.infra.text.json.JsonUtils;
import com.kjrepo.infra.text.json.utils.InvokerUtils;
import com.kjrepo.infra.text.json.utils.TypeMapperUtils;

public class ClassValue implements Value<Class<?>> {

	@SuppressWarnings("unchecked")
	@Override
	public <V> V value(Object json, Class<?> objClass) throws Exception {
		if (objClass.isAssignableFrom(json.getClass())) {
			return (V) json;
		}
		if (json instanceof List) {
			if (objClass.isArray()) {
				return (V) ((List<?>) json).stream().map(o -> valueUnchecked(o, objClass.getComponentType()))
						.toArray(i -> (Object[]) Array.newInstance(objClass.getComponentType(), i));
			} else if (objClass == List.class) {
				return (V) Lists.newArrayList((List<?>) json);
			} else if (objClass == Set.class) {
				return (V) Sets.newHashSet((List<?>) json);
			} else if (List.class.isAssignableFrom(objClass)) {
				Type[] aTypes = List.class.getTypeParameters();
				Map<Type, Type> mType = TypeMapperUtils.mapper(objClass).get(List.class);
				List<?> rtn = (List<?>) objClass.getDeclaredConstructor().newInstance();
				((List<?>) json).forEach(o -> rtn.add(ConfigUtils.valueUnchecked(o, mType.get(aTypes[0]))));
				return (V) rtn;
			} else {
				return (V) InvokerUtils.invokeList(null, (List<?>) json);
			}
		} else if (json instanceof Map) {
			if (objClass == Map.class) {
				return (V) Maps.newHashMap((Map<?, ?>) json);
			} else if (Map.class.isAssignableFrom(objClass)) {
				Type[] aTypes = Map.class.getTypeParameters();
				Map<Type, Type> mType = TypeMapperUtils.mapper(objClass).get(Map.class);
				Map<?, ?> rtn = (Map<?, ?>) objClass.getDeclaredConstructor().newInstance();
				((Map<?, ?>) json).forEach((k, v) -> {
					rtn.put(ConfigUtils.valueUnchecked(k, mType.get(aTypes[0])),
							ConfigUtils.valueUnchecked(v, mType.get(aTypes[1])));
				});
				return (V) rtn;
			} else {
				return (V) ConfigUtils.config(objClass.getDeclaredConstructor().newInstance(), json);
			}
		} else /* if (json instanceof String) */ {
			String sjson = json + "";
			if (objClass == String.class) {
				return (V) sjson;
			} else if (StringUtils.isEmpty(sjson)) {
				return null;
			} else if (objClass == Class.class) {
				return (V) Class.forName(sjson);
			} else if (objClass.isEnum()) {
				return (V) JsonUtils.value(sjson, objClass);
			} else if (objClass == Byte.class || objClass == byte.class) {
				return (V) Byte.valueOf(sjson);
			} else if (objClass == Short.class || objClass == short.class) {
				return (V) Short.valueOf(sjson);
			} else if (objClass == Integer.class || objClass == int.class) {
				return (V) Integer.valueOf(sjson);
			} else if (objClass == Long.class || objClass == long.class) {
				return (V) Long.valueOf(sjson);
			} else if (objClass == Float.class || objClass == float.class) {
				return (V) Float.valueOf(sjson);
			} else if (objClass == Double.class || objClass == double.class) {
				return (V) Double.valueOf(sjson);
			} else if (objClass == Boolean.class || objClass == boolean.class) {
				return (V) Boolean.valueOf(sjson);
			} else if (objClass == Character.class || objClass == char.class) {
				return (V) Character.valueOf(sjson.charAt(0));
			} else {
				return (V) Class.forName(sjson).getDeclaredConstructor().newInstance();
			}
		} /*
			 * else { return (V) json; }
			 */
	}

}
