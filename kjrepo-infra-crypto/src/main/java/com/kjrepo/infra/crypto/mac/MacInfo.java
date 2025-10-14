package com.kjrepo.infra.crypto.mac;

import javax.crypto.Mac;

import com.kjrepo.infra.crypto.CryptoInfo;

public class MacInfo extends CryptoInfo<Mac> {
	private String padding;
	private String keyAlgorithm;
	private String key;

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

}
