package com.kjrepo.infra.network.ftp;

import org.apache.commons.net.ftp.FTPHTTPClient;

public class KftphttpClientInfo extends KftpBeanInfo<FTPHTTPClient> {
	private String proxyHost;
	private int proxyPort;
	private String proxyUser;
	private String proxyPass;
	private Object encoding;

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public String getProxyPass() {
		return proxyPass;
	}

	public void setProxyPass(String proxyPass) {
		this.proxyPass = proxyPass;
	}

	public Object getEncoding() {
		return encoding;
	}

	public void setEncoding(Object encoding) {
		this.encoding = encoding;
	}
}
