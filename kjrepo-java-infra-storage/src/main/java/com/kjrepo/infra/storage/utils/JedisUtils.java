package com.kjrepo.infra.storage.utils;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.annimon.stream.Stream;
import com.google.common.collect.Maps;
import com.kjrepo.infra.storage.jedis.JedisInfo;
import com.kjrepo.infra.storage.jedis.JedisShardingInfo;
import com.kjrepo.infra.storage.jedis.KjedisFactory;
import com.kjrepo.infra.text.json.ConfigUtils;

import redis.clients.jedis.Connection;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSharding;
import redis.clients.jedis.ShardedPipeline;
import redis.clients.jedis.providers.ShardedConnectionProvider;
import redis.clients.jedis.util.Hashing;

@SuppressWarnings("deprecation")
public class JedisUtils {

	public static JedisPool jedis(JedisInfo info) {
		return new JedisPool(info.getPoolConfig(),
				new KjedisFactory(toHostAndPort(info.getHostAndPort()), toClientConfig(info.getClientConfig())));
	}

	public static JedisPool jedis(GenericObjectPoolConfig<Jedis> poolConfig, PooledObjectFactory<Jedis> factory) {
		return new JedisPool(poolConfig, factory);
	}

	public static JedisSharding jedisSharding(JedisShardingInfo info) {
		return jedisSharding(Stream.of(info.getShards()).map(JedisUtils::toHostAndPort).toList(),
				toClientConfig(info.getClientConfig()), info.getPoolConfig(),
				ALGO.getOrDefault(info.getAlgo(), Hashing.MURMUR_HASH),
				StringUtils.isEmpty(info.getTagPattern()) ? null : Pattern.compile(info.getTagPattern()));
	}

	public static JedisSharding jedisSharding(List<HostAndPort> shards, JedisClientConfig clientConfig,
			GenericObjectPoolConfig<Connection> poolConfig, Hashing algo, Pattern tagPattern) {
		return tagPattern == null
				? new JedisSharding(new ShardedConnectionProvider(shards, clientConfig, poolConfig, algo))
				: new JedisSharding(new ShardedConnectionProvider(shards, clientConfig, poolConfig, algo), tagPattern);
	}

	public static ShardedPipeline jedisShardedPipline(JedisShardingInfo info) {
		return jedisShardedPipline(Stream.of(info.getShards()).map(JedisUtils::toHostAndPort).toList(),
				toClientConfig(info.getClientConfig()), info.getPoolConfig(),
				ALGO.getOrDefault(info.getAlgo(), Hashing.MURMUR_HASH),
				StringUtils.isEmpty(info.getTagPattern()) ? null : Pattern.compile(info.getTagPattern()));
	}

	public static ShardedPipeline jedisShardedPipline(List<HostAndPort> shards, JedisClientConfig clientConfig,
			GenericObjectPoolConfig<Connection> poolConfig, Hashing algo, Pattern tagPattern) {
		return tagPattern == null
				? new ShardedPipeline(new ShardedConnectionProvider(shards, clientConfig, poolConfig, algo))
				: new ShardedPipeline(new ShardedConnectionProvider(shards, clientConfig, poolConfig, algo),
						tagPattern);
	}

	public static <T> T execute(JedisPool pool, Function<Jedis, T> func) {
		return ResourceUtils.poolExecute(pool, func);
	}

	public static void execute(JedisPool pool, Consumer<Jedis> consumer) {
		ResourceUtils.poolExecute(pool, consumer);
	}

	public static <T> T execute(JedisSharding jedis, Function<JedisSharding, T> func) {
		return ResourceUtils.execute(jedis, func);
	}

	public static void execute(JedisSharding jedis, Consumer<JedisSharding> consumer) {
		execute(jedis, j -> {
			consumer.accept(j);
			return null;
		});
//		ResourceUtils.execute(jedis, consumer);
	}

	public static <T> T execute(ShardedPipeline jedis, Function<ShardedPipeline, T> func) {
		return ResourceUtils.execute(jedis, func);
	}

	public static void execute(ShardedPipeline jedis, Consumer<ShardedPipeline> consumer) {
		ResourceUtils.execute(jedis, consumer);
	}

	private static final Map<String, Hashing> ALGO = Maps.newHashMap();
	static {
		ALGO.put("MURMUR_HASH", Hashing.MURMUR_HASH);
		ALGO.put("MD5", Hashing.MD5);
	}

	public static boolean register(String name, Hashing hashing) {
		return ALGO.putIfAbsent(name, hashing) == null;
	}

	public static JedisClientConfig toClientConfig(Map<String, Object> clientConfig) {
		return ConfigUtils.config(DefaultJedisClientConfig.builder(), clientConfig).build();
	}

	public static HostAndPort toHostAndPort(Map<String, Object> map) {
		return new HostAndPort((String) map.get("host"), ((Integer) map.get("port")).intValue());
	}

}
