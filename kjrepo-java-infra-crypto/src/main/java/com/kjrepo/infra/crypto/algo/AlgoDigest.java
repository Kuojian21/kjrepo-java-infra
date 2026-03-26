package com.kjrepo.infra.crypto.algo;

public enum AlgoDigest {
	MD5("MD5"), //
	SHA_1("SHA-1"), //
	SHA_256("SHA-256");

	private final String algorithm;

	AlgoDigest(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getAlgorithm() {
		return algorithm;
	}

}
