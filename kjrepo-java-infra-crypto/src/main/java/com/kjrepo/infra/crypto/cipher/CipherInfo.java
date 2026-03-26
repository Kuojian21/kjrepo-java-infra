package com.kjrepo.infra.crypto.cipher;

import javax.crypto.Cipher;

import com.kjrepo.infra.crypto.CryptoInfo;

public class CipherInfo extends CryptoInfo<Cipher> {

	private String key;
	private String keyAlgorithm;
	private CipherInfoKeyType keyType;
	private String padding;

	public String getPadding() {
		return padding;
	}

	public void setPadding(String padding) {
		this.padding = padding;
	}

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

	public CipherInfoKeyType getKeyType() {
		return keyType;
	}

	public void setKeyType(CipherInfoKeyType keyType) {
		this.keyType = keyType;
	}

}