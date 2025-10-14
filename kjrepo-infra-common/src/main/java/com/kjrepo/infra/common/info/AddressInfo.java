package com.kjrepo.infra.common.info;

import java.net.InetSocketAddress;
import java.util.Objects;

public class AddressInfo {

	public static AddressInfo address(String host, int port) {
		AddressInfo address = new AddressInfo();
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

	public InetSocketAddress toInetSocketAddress() {
		return new InetSocketAddress(this.host, this.port);
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
		AddressInfo other = (AddressInfo) obj;
		return Objects.equals(host, other.host) && port == other.port;
	}

}
