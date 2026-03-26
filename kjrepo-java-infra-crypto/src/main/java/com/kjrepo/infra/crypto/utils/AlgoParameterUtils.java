package com.kjrepo.infra.crypto.utils;

import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Base64;

import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;

public class AlgoParameterUtils {

	public static IvParameterSpec loadIvp(String padding) {
		return loadIvp(Base64.getDecoder().decode(padding.getBytes()));
	}

	public static IvParameterSpec loadIvp(byte[] padding) {
		return new IvParameterSpec(padding);
	}

	public static RSAKeyGenParameterSpec loadRsa(int keysize, BigInteger publicExponent,
			AlgorithmParameterSpec keyParams) {
		return new RSAKeyGenParameterSpec(keysize, publicExponent, keyParams);
	}

	public static DSAParameterSpec loadDsa(BigInteger p, BigInteger q, BigInteger g) {
		return new DSAParameterSpec(p, q, g);
	}

	public static GCMParameterSpec loadDsa(int tLen, byte[] src) {
		return new GCMParameterSpec(tLen, src);
	}

	public static PBEParameterSpec loadPbep(byte[] salt, int iterationCount, AlgorithmParameterSpec paramSpec) {
		return new PBEParameterSpec(salt, iterationCount, paramSpec);
	}

}
