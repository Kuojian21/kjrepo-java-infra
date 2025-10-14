package com.kjrepo.infra.crypto.cipher;

import javax.crypto.Cipher;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.kjrepo.infra.crypto.CryptoPoolInfo;

public class CipherPoolInfo implements CryptoPoolInfo<Cipher, CipherInfo> {
	private GenericObjectPoolConfig<Cipher> poolConfig;
	private CipherInfo info;

	public GenericObjectPoolConfig<Cipher> getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(GenericObjectPoolConfig<Cipher> poolConfig) {
		this.poolConfig = poolConfig;
	}

	public CipherInfo getInfo() {
		return info;
	}

	public void setInfo(CipherInfo info) {
		this.info = info;
	}

}
