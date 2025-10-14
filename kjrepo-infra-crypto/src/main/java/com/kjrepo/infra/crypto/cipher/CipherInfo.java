package com.kjrepo.infra.crypto.cipher;

import javax.crypto.Cipher;

import com.kjrepo.infra.crypto.CryptoInfo;

public class CipherInfo extends CryptoInfo<Cipher> {

	private KEYTYPE keyType;
	private String padding;
	private String keyAlgorithm;
	private String key;

	public KEYTYPE getKeyType() {
		return keyType;
	}

	public void setKeyType(KEYTYPE keyType) {
		this.keyType = keyType;
	}

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

	public enum KEYTYPE {
		KEY, PUBKEY, PRIKEY
	}

}