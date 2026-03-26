package com.kjrepo.infra.crypto.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AlgoKeyUtils {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static SecretKey loadKey(String keyAlgorithm, String key) {
		return loadKey(keyAlgorithm, Base64.getDecoder().decode(key));
	}

	public static PublicKey loadPublicKey(String algorithm, String key) {
		return loadPublicKey(algorithm, Base64.getDecoder().decode(key));
	}

	public static PrivateKey loadPrivateKey(String algorithm, String key) {
		return loadPrivateKey(algorithm, Base64.getDecoder().decode(key));
	}

	public static SecretKey loadKey(String algorithm, byte[] key) {
		try {
			return SecretKeyFactory.getInstance(algorithm).generateSecret(new DESedeKeySpec(key));
		} catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException e) {
			return new SecretKeySpec(key, algorithm);
		}
	}

	public static PublicKey loadPublicKey(String algorithm, byte[] key) {
		try {
			return KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(key));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static PrivateKey loadPrivateKey(String algorithm, byte[] key) {
		try {
			return KeyFactory.getInstance(algorithm).generatePrivate(new PKCS8EncodedKeySpec(key));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static SecretKey generateKey(String algorithm) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
			kgen.init(new SecureRandom());
			return kgen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static SecretKey generateKey(String algorithm, int keysize) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
			kgen.init(new SecureRandom());
			kgen.init(keysize);
			return kgen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static SecretKey generateKey(String algorithm, AlgorithmParameterSpec params) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
			kgen.init(new SecureRandom());
			kgen.init(params);
			return kgen.generateKey();
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		}
	}

	public static SecretKey generateKey(String algorithm, int keysize, AlgorithmParameterSpec params) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
			kgen.init(new SecureRandom());
			kgen.init(keysize);
			kgen.init(params);
			return kgen.generateKey();
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		}
	}

	public static KeyPair generateKeyPair(String algorithm, Integer keysize) {
		try {
			KeyPairGenerator kgen = KeyPairGenerator.getInstance(algorithm);
			kgen.initialize(keysize, new SecureRandom());
			return kgen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static KeyPair generateKeyPair(String algorithm, AlgorithmParameterSpec params) {
		try {
			KeyPairGenerator kgen = KeyPairGenerator.getInstance(algorithm);
			kgen.initialize(params, new SecureRandom());
			return kgen.generateKeyPair();
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		}
	}

}