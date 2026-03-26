package com.kjrepo.infra.server.jetty;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.google.common.collect.Maps;
import com.kjrepo.infra.common.logger.LoggerUtils;
import com.kjrepo.infra.common.term.TermHelper;
import com.kjrepo.infra.text.json.ConfigUtils;

import jakarta.servlet.Servlet;

public abstract class JettyServer {

	public static JettyServer spring(String contextPath, List<Class<?>> components) {
		return new JettySpringServer(contextPath, components);
	}

	public static JettyWebappServer webapp(String contextPath, String warDir) {
		return new JettyWebappServer(contextPath, warDir);
	}

	public static JettyStandaloneServer standalone(String contextPath, Map<String, Servlet> servlets) {
		return new JettyStandaloneServer(contextPath, servlets);
	}

	private static final String JETTY = "kjrepo.jetty";
	private static final String JETTY_THEADPOOL = "kjrepo.jetty.threadpool";
	private static final String JETTY_SERVER = "kjrepo.jetty.server";

	protected final Logger logger = LoggerUtils.logger(getClass());

	public void startup(int port) throws Exception {
		startup(server -> {
			ServerConnector connector = new ServerConnector(server);
			connector.setPort(port);

			server.addConnector(connector);
			ServletContextHandler context = context();
			server.setHandler(context);
		});
	}

	public void startup(int sslPort, String keyStorePath, String keyStorePassword, String keyManagerPassword)
			throws Exception {
		startup(server -> {
			SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
			sslContextFactory.setKeyStorePath(keyStorePath);
			sslContextFactory.setKeyStorePassword(keyStorePassword);
			sslContextFactory.setKeyManagerPassword(keyManagerPassword);

			ServerConnector connector = new ServerConnector(server,
					new SslConnectionFactory(sslContextFactory, "http/1.1"),
					new HttpConnectionFactory(new HttpConfiguration()));
			connector.setPort(sslPort);

			server.addConnector(connector);
			ServletContextHandler context = context();
			server.setHandler(context);
		});
	}

	public void startup(Consumer<Server> init) throws Exception {
		Map<String, Object> props = Maps.newHashMap();
		Optional.ofNullable(this.getClass().getClassLoader().getResource("jetty/jetty.properties")).ifPresent(url -> {
			try (InputStream in = url.openStream()) {
				Properties p = new Properties();
				p.load(in);
				p.forEach((key, val) -> {
					props.put(key.toString(), val);
				});
			} catch (IOException e) {
				logger.info("{}", e);
			}
		});

		Stream.of(System.getProperties()).filter(p -> p.getKey() instanceof String)
				.filter(p -> p.getKey().toString().startsWith(JETTY)).forEach(e -> {
					props.put(e.getKey().toString(), e.getValue());
				});
		Map<String, Object> threadPoolInfo = Stream.of(props).filter(p -> p.getKey().startsWith(JETTY_THEADPOOL))
				.collect(
						Collectors.toMap(p -> p.getKey().substring(JETTY_THEADPOOL.length() + 1), Map.Entry::getValue));
		Map<String, Object> serverInfo = Stream.of(props).filter(p -> p.getKey().startsWith(JETTY_SERVER))
				.collect(Collectors.toMap(p -> p.getKey().substring(JETTY_SERVER.length() + 1), Map.Entry::getValue));
		serverInfo.putIfAbsent("stopTimeout", 30000);
		serverInfo.putIfAbsent("stopAtShutdown", true);

		ThreadPool threadPool = (ThreadPool) Class
				.forName(Optional.ofNullable(props.get(JETTY_THEADPOOL)).filter(Objects::nonNull).map(Object::toString)
						.map(String::trim).filter(StringUtils::isNotEmpty).orElseGet(() -> {
							threadPoolInfo.putIfAbsent("name", "jetty");
							threadPoolInfo.putIfAbsent("minThreads", 10);
							threadPoolInfo.putIfAbsent("maxThreads", 100);
							threadPoolInfo.putIfAbsent("idleTimeout", 60000);
							return "org.eclipse.jetty.util.thread.QueuedThreadPool";
						}))
				.getConstructor(new Class<?>[] {}).newInstance(new Object[] {});

		ConfigUtils.config(threadPool, threadPoolInfo);
		Server server = new Server(threadPool);
		ConfigUtils.config(server, serverInfo);

		init.accept(server);

//		connectors.apply(server).forEach(server::addConnector);
//
//		ServletContextHandler context = context();
//		context.setContextPath(this.contextPath);
//		server.setHandler(context);
		server.start();
		TermHelper.addTerm("jetty", () -> server.stop());
		logger.info("jetty has launched successlly!!! ");

//		server.join();
	}

	protected abstract ServletContextHandler context();

}
