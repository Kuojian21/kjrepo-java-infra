package com.kjrepo.infra.storage.db.repository;

import java.io.Closeable;
import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.cluster.ClusterFactory;
import com.kjrepo.infra.cluster.ClusterInfo;
import com.kjrepo.infra.cluster.standby.Standby;
import com.kjrepo.infra.cluster.standby.StandbyFactory;
import com.kjrepo.infra.cluster.standby.StandbyInfo;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.utils.StackUtils;
import com.kjrepo.infra.common.utils.ProxyUtils;
import com.kjrepo.infra.register.context.RegisterFactory;
import com.kjrepo.infra.storage.db.jdbc.Kjdbc;
import com.kjrepo.infra.storage.db.jdbc.KjdbcCluster;
import com.kjrepo.infra.storage.db.jdbc.KjdbcClusterImpl;
import com.kjrepo.infra.storage.db.jdbc.KjdbcImpl;

public class KjdbcRepositoryFactory {

	public static <T, I> Kjdbc<T> jdbc(Class<I> iclazz, String key, Function<I, DataSource> mapper, Class<T> clazz) {
		return new KjdbcImpl<T>(clazz) {
			@Override
			public NamedParameterJdbcOperations jdbcTemplate() {
				return jdbc(iclazz, key, mapper);
			}
		};
	}

	public static <I> NamedParameterJdbcTemplate jdbc(Class<I> clazz, String key, Function<I, DataSource> mapper) {
		return new NamedParameterJdbcTemplate(
				mapper.apply(RegisterFactory.getContext(StackUtils.firstBusinessInvokerClassname()).getRegister(clazz).get(key)));
	}

	public static <T, I, S extends StandbyInfo<I>> Kjdbc<T> standby(Class<S> sclazz, Function<I, DataSource> mapper,
			String key, Class<T> clazz) {
		return new KjdbcImpl<>(clazz) {
			@Override
			public NamedParameterJdbcOperations jdbcTemplate() {
				return standby(sclazz, key, mapper);
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static <I, S extends StandbyInfo<I>> NamedParameterJdbcTemplate standby(Class<S> sclazz, String key,
			Function<I, DataSource> mapper) {
		Standby<NamedParameterJdbcOperations> standby = StandbyFactory.standby(NamedParameterJdbcOperations.class,
				sclazz, key, info -> new NamedParameterJdbcTemplate(mapper.apply((I) info)), rs -> {
					DataSource ds = ((NamedParameterJdbcTemplate) rs).getJdbcTemplate().getDataSource();
					if (ds instanceof Closeable) {
						try {
							((Closeable) ds).close();
						} catch (IOException e) {
							LoggerUtils.logger(KjdbcRepositoryFactory.class).error("", e);
						}
					}
				}).get();
		return standby(standby);
	}

	public static NamedParameterJdbcTemplate standby(Standby<NamedParameterJdbcOperations> standby) {
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

	public static <T, K, I, C extends ClusterInfo<I>> KjdbcCluster<T, K> cluster(Class<C> cclazz, String key,
			Function<I, DataSource> mapper, Class<T> clazz, Function<K, String> sharding) {
		Cluster<NamedParameterJdbcOperations> cluster = cluster(cclazz, key, mapper);
		return new KjdbcClusterImpl<>(clazz) {
			@Override
			public Cluster<NamedParameterJdbcOperations> cluster() {
				return cluster;
			}
		};
	}

	public static <I, C extends ClusterInfo<I>> Cluster<NamedParameterJdbcOperations> cluster(Class<C> cclazz,
			String key, Function<I, DataSource> mapper) {
		return ClusterFactory.cluster(NamedParameterJdbcOperations.class, cclazz, key,
				info -> new NamedParameterJdbcTemplate(mapper.apply(info.getInfo())), rs -> {
					DataSource ds = ((NamedParameterJdbcTemplate) rs).getJdbcTemplate().getDataSource();
					if (ds instanceof Closeable) {
						try {
							((Closeable) ds).close();
						} catch (IOException e) {
							LoggerUtils.logger(KjdbcRepositoryFactory.class).error("", e);
						}
					}
				}).get();
	}

}
