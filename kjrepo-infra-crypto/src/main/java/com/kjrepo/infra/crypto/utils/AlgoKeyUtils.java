package com.kjrepo.infra.crypto.utils;

import java.security.InvalidAlgorithmParameterException;
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

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.kjrepo.infra.crypto.CryptoRuntimeException;

public class AlgoKeyUtils {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static SecretKey loadKey(String keyAlgorithm, byte[] key) {
//		try {
		return new SecretKeySpec(key, keyAlgorithm);
//			return SecretKeyFactory.getInstance(keyAlgorithm).generateSecret(new DESedeKeySpec(key));
//		} catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException e) {
//			throw new CryptRuntimeException(e);
//		}
	}

	public static PublicKey loadPublicKey(String algorithm, byte[] key) {
		try {
			return KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(key));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static PrivateKey loadPrivateKey(String algorithm, byte[] key) {
		try {
			return KeyFactory.getInstance(algorithm).generatePrivate(new PKCS8EncodedKeySpec(key));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static IvParameterSpec loadIvp(byte[] padding) {
		return new IvParameterSpec(padding);
	}

	public static SecretKey loadKey(String keyAlgorithm, String key) {
		return loadKey(keyAlgorithm, Base64Utils.decode(key));
	}

	public static PublicKey loadPublicKey(String algorithm, String key) {
		return loadPublicKey(algorithm, Base64Utils.decode(key));
	}

	public static PrivateKey loadPrivateKey(String algorithm, String key) {
		return loadPrivateKey(algorithm, Base64Utils.decode(key));
	}

	public static IvParameterSpec loadIvp(String padding) {
		return loadIvp(padding.getBytes());
	}

	public static SecretKey generateKey(String algorithm) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
			kgen.init(new SecureRandom());
			return kgen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static SecretKey generateKey(String algorithm, int keysize) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
			kgen.init(new SecureRandom());
			kgen.init(keysize);
			return kgen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static SecretKey generateKey(String algorithm, AlgorithmParameterSpec params) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
			kgen.init(new SecureRandom());
			kgen.init(params);
			return kgen.generateKey();
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
			throw new CryptoRuntimeException(e);
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
			throw new CryptoRuntimeException(e);
		}
	}

	public static KeyPair generateKeyPair(String algorithm, Integer keysize) {
		try {
			KeyPairGenerator kgen = KeyPairGenerator.getInstance(algorithm);
			kgen.initialize(keysize, new SecureRandom());
			return kgen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static KeyPair generateKeyPair(String algorithm, AlgorithmParameterSpec params) {
		try {
			KeyPairGenerator kgen = KeyPairGenerator.getInstance(algorithm);
			kgen.initialize(params, new SecureRandom());
			return kgen.generateKeyPair();
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
			throw new CryptoRuntimeException(e);
		}
	}

}