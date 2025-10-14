package com.kjrepo.infra.crypto.digest;

import java.security.MessageDigest;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.kjrepo.infra.crypto.CryptoPoolInfo;

public class DigestPoolInfo implements CryptoPoolInfo<MessageDigest, DigestInfo> {
	private GenericObjectPoolConfig<MessageDigest> poolConfig;
	private DigestInfo info;

	public GenericObjectPoolConfig<MessageDigest> getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(GenericObjectPoolConfig<MessageDigest> poolConfig) {
		this.poolConfig = poolConfig;
	}

	public DigestInfo getInfo() {
		return info;
	}

	public void setInfo(DigestInfo info) {
		this.info = info;
	}

}
