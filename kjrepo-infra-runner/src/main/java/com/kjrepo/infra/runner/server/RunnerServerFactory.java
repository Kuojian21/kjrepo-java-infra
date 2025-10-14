package com.kjrepo.infra.runner.server;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.annimon.stream.Stream;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.lazy.LazySupplier;
import com.kjrepo.infra.runner.Runner;
import com.kjrepo.infra.runner.server.args.Args;
//import com.kjrepo.infra.spring.SpringBeanFactory;
import com.kjrepo.infra.text.json.utils.TypeMapperUtils;

public class RunnerServerFactory {

	private static final Map<Class<? extends Runner>, RunnerServer<? extends Runner>> servers = Maps.newConcurrentMap();
	static {
//		SpringBeanFactory.getBeans(RunnerServer.class).values().forEach(server -> register(server));
		Stream.of(ServiceLoader.load(RunnerServer.class)).forEach(server -> register(server));
	}

	@SuppressWarnings("unchecked")
	public static void register(RunnerServer<?> server) {
		servers.put((Class<? extends Runner>) TypeMapperUtils.mapper(server.getClass()).get(RunnerServer.class)
				.get(RunnerServer.class.getTypeParameters()[0]), server);
	}

	public static List<RunnerServer<?>> servers() {
		return Lists.newArrayList(servers.values());
	}

	public static void args(Args args) {
		RunnerServerFactory.servers().forEach(server -> {
			server.setCommandLine(LazySupplier.wrap(() -> {
				try {
					return new DefaultParser().parse(server.options(new Options()), args.args(server.aprefix()), true);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}));
		});
	}

	@SuppressWarnings("unchecked")
	public static <R extends Runner> RunnerServer<R> server(Class<R> clazz) {
		if (clazz == null) {
			return null;
		}
		RunnerServer<R> server = (RunnerServer<R>) servers.get(clazz);
		if (server == null && clazz.getSuperclass() != null) {
			if (Runner.class.isAssignableFrom(clazz.getSuperclass())) {
				server = server((Class<R>) clazz.getSuperclass());
			}
		}
		if (server == null) {
			for (Class<?> iclazz : clazz.getInterfaces()) {
				if (Runner.class.isAssignableFrom(iclazz)) {
					server = server((Class<R>) iclazz);
					if (server != null) {
						break;
					}
				}
			}
		}
		return server;
	}

}
