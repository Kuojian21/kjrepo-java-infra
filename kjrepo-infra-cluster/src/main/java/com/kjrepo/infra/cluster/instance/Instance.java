package com.kjrepo.infra.cluster.instance;

import java.io.Closeable;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Supplier;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.common.utils.ProxyUtils;

public class Instance<R> implements Supplier<R>, Closeable {

	public static <R> Instance<R> of(String name, Class<R> clazz, R resource) {
		return new Instance<R>(name, clazz, resource, null);
	}

	public static <R> Instance<R> of(String name, Class<R> clazz, R resource, Consumer<R> release) {
		return new Instance<R>(name, clazz, resource, release);
	}

	private final String name;
	private final Class<R> clazz;
	private final R resource;
	private final GenericObjectPool<R> pool;
	private final LazySupplier<R> instance;
	private final Consumer<R> release;

	public Instance(String name, Class<R> clazz, R resource) {
		this(name, clazz, resource, null);
	}

	public Instance(String name, Class<R> clazz, R resource, Consumer<R> release) {
		super();
		this.name = name;
		this.clazz = clazz;
		this.resource = resource;
		this.pool = null;
		this.instance = LazySupplier.wrap(() -> {
			return ProxyUtils.proxy(this.clazz, (obj, method, args, proxy) -> {
				return method.invoke(this.resource, args);
			});
		});
		this.release = release;
	}

	public Instance(String name, Class<R> clazz, Supplier<R> supplier, GenericObjectPoolConfig<R> poolConfig) {
		this(name, clazz, supplier, poolConfig, null);
	}

	public Instance(String name, Class<R> clazz, Supplier<R> supplier, GenericObjectPoolConfig<R> poolConfig,
			Consumer<R> release) {
		super();
		this.name = name;
		this.clazz = clazz;
		this.resource = null;
		if (poolConfig == null) {
			poolConfig = new GenericObjectPoolConfig<R>();
		}
		this.pool = new GenericObjectPool<R>(new BasePooledObjectFactory<R>() {

			@Override
			public R create() throws Exception {
				return (R) supplier.get();
			}

			@Override
			public PooledObject<R> wrap(R obj) {
				return new DefaultPooledObject<>(obj);
			}

			@Override
			public void destroyObject(final PooledObject<R> obj) throws Exception {
				close(obj.getObject());
			}

		}, poolConfig);

		this.instance = LazySupplier.wrap(() -> {
			return ProxyUtils.proxy(this.clazz, (obj, method, args, proxy) -> {
				R resource = this.pool.borrowObject();
				try {
					return method.invoke(resource, args);
				} finally {
					this.pool.returnObject(resource);
				}
			});
		});
		this.release = release;
	}

	@Override
	public R get() {
		return this.instance.get();
	}

	public String name() {
		return this.name;
	}

	@Override
	public void close() {
		if (this.resource != null) {
			this.close(resource);
		}
		if (this.pool != null) {
			this.pool.close();
		}
	}

	public void close(R resoruce) {
		this.release.accept(resoruce);
	}

}
