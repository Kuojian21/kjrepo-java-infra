package com.kjrepo.infra.server.jetty;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyWebappServer extends JettyServer {

	private final String contextPath;
	private final String warDir;

	public JettyWebappServer(String contextPath, String warDir) {
		this.contextPath = contextPath;
		this.warDir = warDir;
	}

	@Override
	public ServletContextHandler context() {
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath(this.contextPath);
		webapp.setWar(this.warDir);
		webapp.setExtractWAR(true);
		webapp.setCopyWebDir(true);
		webapp.setCopyWebInf(true);
		return webapp;
	}

}
