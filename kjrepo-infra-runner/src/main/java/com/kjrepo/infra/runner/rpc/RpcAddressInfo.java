package com.kjrepo.infra.runner.rpc;

import java.util.Objects;

public class RpcAddressInfo {

	public static RpcAddressInfo address(String host, int port) {
		RpcAddressInfo address = new RpcAddressInfo();
		address.host = host;
		address.port = port;
		return address;
	}

	private String host;
	private int port;

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

	@Override
	public int hashCode() {
		return Objects.hash(host, port);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RpcAddressInfo other = (RpcAddressInfo) obj;
		return Objects.equals(host, other.host) && port == other.port;
	}

}
