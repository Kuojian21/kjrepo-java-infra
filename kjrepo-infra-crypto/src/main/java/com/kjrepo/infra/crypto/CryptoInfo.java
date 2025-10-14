package com.kjrepo.infra.crypto;

import com.kjrepo.infra.common.executor.PooledInfoDefault;

public class CryptoInfo<T> extends PooledInfoDefault<T> {

	private String algorithm;

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
}
