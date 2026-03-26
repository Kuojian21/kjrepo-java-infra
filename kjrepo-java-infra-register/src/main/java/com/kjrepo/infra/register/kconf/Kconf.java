package com.kjrepo.infra.register.kconf;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;
import com.annimon.stream.function.ThrowableConsumer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kjrepo.infra.text.json.ConfigUtils;

@SuppressWarnings("unchecked")
public interface Kconf<T> extends Supplier<T> {

	static <T> Kconf<T> of(String key, Class<?> clazz) {
		return of(key, clazz, (T) null);
	}

	static <T> Kconf<T> of(String key, Class<?> clazz, T defValue) {
		return of(key, clazz, obj -> obj == null ? defValue : (T) obj, null);
	}

	static <T> Kconf<List<T>> ofList(String key, Class<T> clazz) {
		return ofList(key, clazz, Lists.newArrayList());
	}

	static <T> Kconf<List<T>> ofList(String key, Class<T> clazz, List<T> def) {
		return of(key, List.class, obj -> {
			return Stream.of((List<?>) Optional.ofNullable(obj).orElse(def))
					.map(o -> ConfigUtils.<T>valueUnchecked(o, clazz)).collect(Collectors.toList());
		});
	}

	static <T> Kconf<Set<T>> ofSet(String key, Class<T> clazz) {
		return ofSet(key, clazz, Sets.newHashSet());
	}

	static <T> Kconf<Set<T>> ofSet(String key, Class<T> clazz, Set<T> def) {
		return of(key, Set.class, obj -> {
			return Stream.of((Set<?>) Optional.ofNullable(obj).orElse(def))
					.map(o -> ConfigUtils.<T>valueUnchecked(o, clazz)).collect(Collectors.toSet());
		});
	}

	static <T> Kconf<Map<String, T>> ofMap(String key, Class<T> clazz) {
		return ofMap(key, clazz, Maps.newHashMap());
	}

	static <T> Kconf<Map<String, T>> ofMap(String key, Class<T> clazz, Map<String, T> def) {
		return of(key, Map.class, obj -> {
			return Stream.of((Map<String, ?>) Optional.ofNullable(obj).orElse(def))
					.collect(Collectors.toMap(Map.Entry::getKey, e -> ConfigUtils.valueUnchecked(e.getValue(), clazz)));
		});
	}

	static <T, V> Kconf<T> of(String key, Class<V> clazz, Function<V, T> mapper) {
		return of(key, clazz, mapper, null);
	}

	static <T, V> Kconf<T> of(String key, Class<V> clazz, Function<V, T> mapper,
			ThrowableConsumer<T, Throwable> release) {
		return new KconfImpl<>(key, clazz, mapper, release);
	}

}
