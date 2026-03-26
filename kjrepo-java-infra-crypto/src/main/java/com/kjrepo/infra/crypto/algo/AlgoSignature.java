package com.kjrepo.infra.crypto.algo;

public enum AlgoSignature {
	SHA1withDSA_1024("SHA1withDSA", "DSA", 1024), //
	SHA1withDSA_2048("SHA1withDSA", "DSA", 2048), //
	SHA1withRSA_1024("SHA1withRSA", "RSA", 1024), //
	SHA1withRSA_2048("SHA1withRSA", "RSA", 2048), //
	SHA256withRSA_1024("SHA256withRSA", "RSA", 1024), //
	SHA256withRSA_2048("SHA256withRSA", "RSA", 2048);

	private final String algorithm;
	private final int keysize;
	private final String keyAlgorithm;

	AlgoSignature(String algorithm, String keyAlgorithm, int keysize) {
		this.algorithm = algorithm;
		this.keyAlgorithm = keyAlgorithm;
		this.keysize = keysize;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public String getKeyAlgorithm() {
		return keyAlgorithm;
	}

	public int getKeysize() {
		return keysize;
	}

}
