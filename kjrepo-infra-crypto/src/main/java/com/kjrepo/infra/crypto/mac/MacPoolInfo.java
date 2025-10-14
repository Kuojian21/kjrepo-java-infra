package com.kjrepo.infra.crypto.mac;

import javax.crypto.Mac;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.kjrepo.infra.crypto.CryptoPoolInfo;

public class MacPoolInfo implements CryptoPoolInfo<Mac, MacInfo> {
	private GenericObjectPoolConfig<Mac> poolConfig;
	private MacInfo info;

	public GenericObjectPoolConfig<Mac> getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(GenericObjectPoolConfig<Mac> poolConfig) {
		this.poolConfig = poolConfig;
	}

	public MacInfo getInfo() {
		return info;
	}

	public void setInfo(MacInfo info) {
		this.info = info;
	}

}
