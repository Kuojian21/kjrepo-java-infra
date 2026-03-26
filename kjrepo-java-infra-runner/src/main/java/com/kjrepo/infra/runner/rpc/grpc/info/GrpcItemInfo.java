package com.kjrepo.infra.runner.rpc.grpc.info;

import java.util.Objects;

public class GrpcItemInfo {

	public static GrpcItemInfo address(String host, int port) {
		GrpcItemInfo address = new GrpcItemInfo();
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
		GrpcItemInfo other = (GrpcItemInfo) obj;
		return Objects.equals(host, other.host) && port == other.port;
	}

}
