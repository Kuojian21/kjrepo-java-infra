package com.kjrepo.infra.network.jsch;

import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.kjrepo.infra.common.executor.PooledInfoDefault;

public class JschBeanInfo<B extends Channel> extends PooledInfoDefault<B> {

	private String host;
	private int port;
	private String username;
	private Properties sshConfig = new Properties();
	private int connectTimeout;

	private String password;

	private String prvkey;
	private String pubkey;
	private String passphrase;

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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Properties getSshConfig() {
		return sshConfig;
	}

	public void setSshConfig(Properties sshConfig) {
		this.sshConfig = sshConfig;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPrvkey() {
		return prvkey;
	}

	public void setPrvkey(String prvkey) {
		this.prvkey = prvkey;
	}

	public String getPubkey() {
		return pubkey;
	}

	public void setPubkey(String pubkey) {
		this.pubkey = pubkey;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}
}