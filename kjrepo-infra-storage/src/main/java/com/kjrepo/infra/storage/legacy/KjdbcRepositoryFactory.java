package com.kjrepo.infra.storage.legacy;

import java.io.Closeable;
import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.cluster.Master;
import com.kjrepo.infra.cluster.impl.ClusterFactory;
import com.kjrepo.infra.cluster.impl.MasterFactory;
import com.kjrepo.infra.cluster.info.ClusterInfo;
import com.kjrepo.infra.cluster.info.MasterInfo;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.utils.StackUtils;
import com.kjrepo.infra.common.utils.ProxyUtils;
import com.kjrepo.infra.register.context.RegisterFactory;
import com.kjrepo.infra.storage.db.jdbc.Kjdbc;
import com.kjrepo.infra.storage.db.jdbc.KjdbcHolder;
import com.kjrepo.infra.storage.db.jdbc.KjdbcImpl;

public class KjdbcRepositoryFactory {

	public static <T, I> Kjdbc<T> jdbc(Class<I> iclazz, String key, Function<I, DataSource> mapper, Class<T> clazz) {
		return new KjdbcImpl<T>(clazz) {
			@Override
			public KjdbcHolder holder(boolean master) {
				return KjdbcHolder.of(jdbc(iclazz, key, mapper));
			}
		};
	}

	public static <I> NamedParameterJdbcTemplate jdbc(Class<I> clazz, String key, Function<I, DataSource> mapper) {
		return new NamedParameterJdbcTemplate(mapper.apply(
				RegisterFactory.getContext(StackUtils.firstBusinessInvokerClassname()).getRegister(clazz).get(key)));
	}

	public static <T, I, S extends MasterInfo<I>> Kjdbc<T> standby(Class<S> sclazz, Function<I, DataSource> mapper,
			String key, Class<T> clazz) {
		return new KjdbcImpl<>(clazz) {
			@Override
			public KjdbcHolder holder(boolean master) {
				return KjdbcHolder.of(standby(sclazz, key, mapper));
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static <I, S extends MasterInfo<I>> NamedParameterJdbcTemplate standby(Class<S> sclazz, String key,
			Function<I, DataSource> mapper) {
		Master<NamedParameterJdbcOperations> standby = MasterFactory.<NamedParameterJdbcOperations, I, S>master(sclazz,
				key, info -> new NamedParameterJdbcTemplate(mapper.apply((I) info)), rs -> {
					DataSource ds = ((NamedParameterJdbcTemplate) rs).getJdbcTemplate().getDataSource();
					if (ds instanceof Closeable) {
						try {
							((Closeable) ds).close();
						} catch (IOException e) {
							LoggerUtils.logger(KjdbcRepositoryFactory.class).error("", e);
						}
					}
				});
		return standby(standby);
	}

	public static NamedParameterJdbcTemplate standby(Master<NamedParameterJdbcOperations> standby) {
		return new NamedParameterJdbcTemplate(
				(JdbcOperations) ProxyUtils.proxy(JdbcOperations.class, (obj, method, args, proxy) -> {
					String mname = method.getName();
					if (mname.startsWith("query")) {
						return method.invoke(standby.slave(), args);
					} else if (mname.startsWith("execute")) {
						return method
								.invoke(args[0].toString().trim().toLowerCase().startsWith("select") ? standby.slave()
										: standby.master(), args);
					} else {
						return method.invoke(standby.master(), args);
					}
				}));
	}

	public static <T, K, I, C extends ClusterInfo<I>> KjdbcCluster<T> cluster(Class<C> cclazz, String key,
			Function<I, DataSource> mapper, Class<T> clazz, Function<K, String> sharding) {
		Cluster<NamedParameterJdbcTemplate> cluster = cluster(cclazz, key, mapper);
		return new KjdbcClusterImpl<>(clazz) {
			@Override
			public Cluster<NamedParameterJdbcTemplate> cluster() {
				return cluster;
			}
		};
	}

	public static <I, C extends ClusterInfo<I>> Cluster<NamedParameterJdbcTemplate> cluster(Class<C> cclazz, String key,
			Function<I, DataSource> mapper) {
		return ClusterFactory.cluster(cclazz, key,
				info -> new NamedParameterJdbcTemplate(mapper.apply((I) info.getInfo())), rs -> {
					DataSource ds = ((NamedParameterJdbcTemplate) rs).getJdbcTemplate().getDataSource();
					if (ds instanceof Closeable) {
						try {
							((Closeable) ds).close();
						} catch (IOException e) {
							LoggerUtils.logger(KjdbcRepositoryFactory.class).error("", e);
						}
					}
				});
	}

}
