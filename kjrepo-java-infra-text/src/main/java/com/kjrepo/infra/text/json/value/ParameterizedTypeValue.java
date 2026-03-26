package com.kjrepo.infra.text.json.value;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.annimon.stream.Collectors;
import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.kjrepo.infra.text.json.ConfigUtils;
import com.kjrepo.infra.text.json.utils.InvokerUtils;

public class ParameterizedTypeValue extends AbstractValue<ParameterizedType> {

	public ParameterizedTypeValue(Map<Type, Type> mapper) {
		super(mapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> V value(Object json, ParameterizedType type) throws Exception {
		Class<V> rawType = (Class<V>) type.getRawType();
		if (json instanceof List) {
			if (rawType == List.class) {
				return (V) Stream.of((List<?>) json)
						.map(o -> ConfigUtils.valueUnchecked(o, type.getActualTypeArguments()[0], this.mapper()))
						.toList();
			} else if (rawType == Set.class) {
				return (V) Stream.of((List<?>) json)
						.map(o -> ConfigUtils.valueUnchecked(o, type.getActualTypeArguments()[0], this.mapper()))
						.collect(Collectors.toSet());
			} else {
				return (V) InvokerUtils.invokeList(null, (List<?>) json);
			}
		} else if (json instanceof Map) {
			if (rawType == Map.class) {
				return (V) Stream.of(((Map<?, ?>) json).entrySet()).collect(Collectors.toMap(
						e -> ConfigUtils.valueUnchecked(e.getKey(), type.getActualTypeArguments()[0], this.mapper()),
						e -> ConfigUtils.valueUnchecked(e.getValue(), type.getActualTypeArguments()[1],
								this.mapper())));
			} else {
				Type[] atypes = type.getActualTypeArguments();
				Type[] dtypes = rawType.getTypeParameters();
				return ConfigUtils.config(rawType.getDeclaredConstructor().newInstance(), json,
						IntStream.range(0, atypes.length).mapToObj(i -> i).collect(Collectors.toMap(i -> dtypes[i],
								i -> this.mapper().getOrDefault(atypes[i], atypes[i]))));
			}
		} else if (json instanceof String) {
			return ConfigUtils.valueUnchecked(json, rawType, this.mapper());
		} else {
			return ConfigUtils.valueUnchecked(json, rawType, this.mapper());
		}
	}

}
