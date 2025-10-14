package com.kjrepo.infra.network.mail.legacy;

import java.util.Properties;

import javax.mail.Service;

import com.kjrepo.infra.common.executor.PooledInfoDefault;

public class MailInfo<S extends Service> extends PooledInfoDefault<MailSessionHolder<S>> {
	private String protocol;
	private String host;
	private int port = -1;
	private String user;
	private String password;
	private boolean debug;
	private Properties props = new Properties();

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

}
