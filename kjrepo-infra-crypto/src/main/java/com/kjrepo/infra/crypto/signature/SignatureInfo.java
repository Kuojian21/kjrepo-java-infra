package com.kjrepo.infra.crypto.signature;

import java.security.Signature;

import com.kjrepo.infra.crypto.CryptoInfo;

public class SignatureInfo extends CryptoInfo<Signature> {

	private String keyAlgorithm;
	private String key;

	public String getKeyAlgorithm() {
		return keyAlgorithm;
	}

	public void setKeyAlgorithm(String keyAlgorithm) {
		this.keyAlgorithm = keyAlgorithm;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}