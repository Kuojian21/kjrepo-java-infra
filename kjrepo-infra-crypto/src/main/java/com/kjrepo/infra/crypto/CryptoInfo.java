package com.kjrepo.infra.crypto;

import com.kjrepo.infra.executor.pool.AbstractPoolExecutorInfo;

public class CryptoInfo<T> extends AbstractPoolExecutorInfo<T> {

	private String algorithm;

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
}
