package com.kjrepo.infra.register.kconf;

import java.lang.reflect.InvocationTargetException;
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
import com.kjrepo.infra.common.hook.HookHelper;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.register.context.RegisterFactory;
import com.kjrepo.infra.text.json.ConfigUtils;

@SuppressWarnings("unchecked")
public abstract class Kconf<T> implements Supplier<T> {

	public static <T> Kconf<T> conf(String key, Class<?> clazz) {
		return conf(key, clazz, (T) null);
	}

	public static <T> Kconf<T> conf(String key, Class<?> clazz, T defValue) {
		return conf(key, clazz, obj -> obj == null ? defValue : (T) obj, null);
	}

	public static <T> Kconf<List<T>> confList(String key, Class<T> clazz) {
		return conf(key, List.class, obj -> {
			return Stream.of((List<?>) Optional.ofNullable(obj).orElseGet(Lists::newArrayList))
					.map(o -> ConfigUtils.<T>valueUnchecked(o, clazz)).collect(Collectors.toList());
		});
	}

	public static <T> Kconf<Set<T>> confSet(String key, Class<T> clazz) {
		return conf(key, Set.class, obj -> {
			return Stream.of((Set<?>) Optional.ofNullable(obj).orElseGet(Sets::newHashSet))
					.map(o -> ConfigUtils.<T>valueUnchecked(o, clazz)).collect(Collectors.toSet());
		});
	}

	public static <T> Kconf<Map<String, T>> confMap(String key, Class<T> clazz) {
		return conf(key, Map.class, obj -> {
			return Stream.of((Map<String, ?>) Optional.ofNullable(obj).orElseGet(Maps::newHashMap))
					.collect(Collectors.toMap(Map.Entry::getKey, e -> ConfigUtils.valueUnchecked(e.getValue(), clazz)));
		});
	}

	public static <T, V> Kconf<T> conf(String key, Class<V> clazz, Function<V, T> mapper) {
		return conf(key, clazz, mapper, null);
	}

	public static <T, V> Kconf<T> conf(String key, Class<V> clazz, Function<V, T> mapper,
			ThrowableConsumer<T, Throwable> release) {
		return conf(key, (Register<V>) RegisterFactory.getContext().getRegister(clazz), mapper, release);
	}

	public static <T, V> Kconf<T> conf(String key, Register<V> register, Function<V, T> mapper,
			ThrowableConsumer<T, Throwable> release) {
		return new XKconf<>(key, register, mapper, release);
	}

	public static <T, V> Kconf<T> conf(String key, Class<V> clazz, Class<T> rclazz) {
		return conf(key, clazz, rclazz, null);
	}

	private static final Map<Register<?>, LazySupplier<Map<Class<?>, LazySupplier<Kconf<?>>>>> cache = Maps
			.newConcurrentMap();

	public static <T, V> Kconf<T> conf(String key, Class<V> clazz, Class<T> rclazz,
			ThrowableConsumer<T, Throwable> release) {
		Register<V> register = (Register<V>) RegisterFactory.getContext().getRegister(clazz);
		return (Kconf<T>) cache.computeIfAbsent(register, k -> LazySupplier.wrap(Maps::newConcurrentMap)).get()
				.computeIfAbsent(rclazz, k -> {
					return LazySupplier.wrap(() -> conf(key, register, arg -> {
						if (rclazz.isAssignableFrom(clazz)) {
							return (T) arg;
						}
						try {
							return (T) rclazz.getDeclaredConstructor(new Class<?>[] { clazz })
									.newInstance(new Object[] { arg });
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException | NoSuchMethodException | SecurityException e) {
							throw new RuntimeException(e);
						}
					}, release));
				}).get();
	}

	static class XKconf<T, V> extends Kconf<T> {
		private final String key;
		private final LazySupplier<T> conf;
		private final ThrowableConsumer<T, Throwable> release;

		public XKconf(String key, Register<V> register, Function<V, T> mapper,
				ThrowableConsumer<T, Throwable> release) {
			super();
			this.key = key;
			this.conf = LazySupplier.wrap(() -> {
				return mapper.apply(register.get(key));
			});
			this.release = release;
			register.addListener(key, event -> this.refresh());
			if (this.release != null) {
				HookHelper.addHook("kconf", this::refresh);
			}
		}

		public String key() {
			return this.key;
		}

		@Override
		public T get() {
			return this.conf.get();
		}

		public void close() {
			this.refresh();
		}

		private void refresh() {
			this.conf.refresh(this.release);
		}
	}

}
