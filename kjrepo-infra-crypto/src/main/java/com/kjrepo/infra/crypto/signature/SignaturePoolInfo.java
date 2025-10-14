package com.kjrepo.infra.crypto.signature;

import java.security.Signature;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.kjrepo.infra.crypto.CryptoPoolInfo;

public class SignaturePoolInfo implements CryptoPoolInfo<Signature, SignatureInfo> {
	private GenericObjectPoolConfig<Signature> poolConfig;
	private SignatureInfo info;

	public GenericObjectPoolConfig<Signature> getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(GenericObjectPoolConfig<Signature> poolConfig) {
		this.poolConfig = poolConfig;
	}

	public SignatureInfo getInfo() {
		return info;
	}

	public void setInfo(SignatureInfo info) {
		this.info = info;
	}

}
