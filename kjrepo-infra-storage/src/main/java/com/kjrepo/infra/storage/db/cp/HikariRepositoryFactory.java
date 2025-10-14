package com.kjrepo.infra.storage.db.cp;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.annimon.stream.function.Function;
import com.kjrepo.infra.cluster.Cluster;
import com.kjrepo.infra.cluster.ClusterFactory;
import com.kjrepo.infra.cluster.standby.Standby;
import com.kjrepo.infra.cluster.standby.StandbyFactory;
import com.kjrepo.infra.common.utils.ProxyUtils;
import com.kjrepo.infra.register.Register;
import com.kjrepo.infra.storage.db.jdbc.Jdbc;
import com.kjrepo.infra.storage.db.jdbc.JdbcCluster;
import com.kjrepo.infra.storage.db.jdbc.JdbcClusterImpl;
import com.kjrepo.infra.storage.db.jdbc.JdbcImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariRepositoryFactory {

	public static <T> Jdbc<T> jdbc(Register<HikariConfig> register, String key, Class<T> clazz) {
		return new JdbcImpl<>(clazz) {
			@Override
			public NamedParameterJdbcOperations jdbcTemplate() {
				return jdbc(register, key);
			}
		};
	}

	public static NamedParameterJdbcTemplate jdbc(Register<HikariConfig> register, String key) {
		return new NamedParameterJdbcTemplate(new HikariDataSource(register.get(key)));
	}

	public static <T> Jdbc<T> standby(Register<HikariStandbyInfo> register, String key, Class<T> clazz) {
		return new JdbcImpl<>(clazz) {
			@Override
			public NamedParameterJdbcOperations jdbcTemplate() {
				return standby(register, key);
			}
		};
	}

	@SuppressWarnings("resource")
	public static NamedParameterJdbcTemplate standby(Register<HikariStandbyInfo> register, String key) {
		Standby<NamedParameterJdbcOperations> standby = StandbyFactory.standby(NamedParameterJdbcOperations.class,
				HikariStandbyInfo.class, register, key,
				info -> new NamedParameterJdbcTemplate(new HikariDataSource((HikariConfig) info)),
				rs -> ((HikariDataSource) ((NamedParameterJdbcTemplate) rs).getJdbcTemplate().getDataSource()).close());
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

	public static <T, K> JdbcCluster<T, K> cluster(Register<HikariClusterInfo> register, String key, Class<T> clazz,
			Function<K, String> sharding) {
		Cluster<NamedParameterJdbcOperations> cluster = cluster(register, key);
		return new JdbcClusterImpl<>(clazz) {
			@Override
			public Cluster<NamedParameterJdbcOperations> cluster() {
				return cluster;
			}
		};
	}

	@SuppressWarnings({ "resource" })
	public static Cluster<NamedParameterJdbcOperations> cluster(Register<HikariClusterInfo> register, String key) {
		return ClusterFactory.cluster(NamedParameterJdbcOperations.class, register, key,
				info -> new NamedParameterJdbcTemplate(new HikariDataSource((HikariConfig) info.getInfo())),
				jt -> ((HikariDataSource) ((NamedParameterJdbcTemplate) jt).getJdbcTemplate().getDataSource()).close()).get();
	}

}
